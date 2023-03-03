#import "CDVVoxeet.h"
#import <Cordova/CDV.h>
#import <VoxeetSDK/VoxeetSDK.h>
#import <VoxeetUXKit/VoxeetUXKit.h>

static NSString * const kCordovaPluginVersion = @"___CORDOVA_VERSION___";
static NSString * const kCordovaComponentName = @"ios-cordova";

@interface CDVVoxeet()

@property (nonatomic, copy) NSString *consumerKey;
@property (nonatomic, copy) NSString *consumerSecret;
@property (nonatomic, copy) CDVInvokedUrlCommand *refreshAccessTokenCommand;
@property (nonatomic, copy) void (^refreshAccessTokenClosure)(NSString *);
@property (nonatomic, copy) CDVInvokedUrlCommand *conferenceStatusUpdatedCommand;

@end

@implementation CDVVoxeet

- (void)pluginInitialize {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(finishLaunching:) name:UIApplicationDidFinishLaunchingNotification object:nil];
}

- (void)finishLaunching:(NSNotification *)notification {
    NSString *consumerKey = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"VOXEET_CORDOVA_CONSUMER_KEY"];
    NSString *consumerKeyPref = [self.commandDelegate.settings objectForKey:[@"VOXEET_CORDOVA_CONSUMER_KEY" lowercaseString]];
    NSString *consumerSecret = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"VOXEET_CORDOVA_CONSUMER_SECRET"];
    NSString *consumerSecretPref = [self.commandDelegate.settings objectForKey:[@"VOXEET_CORDOVA_CONSUMER_SECRET" lowercaseString]];
    
    if (consumerKey != nil && consumerKey.length != 0 && ![consumerKey isEqualToString:@"null"] &&
        consumerSecret != nil && consumerSecret.length != 0 && ![consumerSecret isEqualToString:@"null"]) {
        _consumerKey = consumerKey;
        _consumerSecret = consumerSecret;
        [self initializeWithConsumerKey:_consumerKey consumerSecret:_consumerSecret];
    } else if (consumerKeyPref != nil && consumerKeyPref.length != 0 && consumerSecretPref != nil && consumerSecretPref.length != 0) {
        _consumerKey = consumerKeyPref;
        _consumerSecret = consumerSecretPref;
        [self initializeWithConsumerKey:_consumerKey consumerSecret:_consumerSecret];
    }
    
    // Observers.
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(conferenceStatusUpdated:) name:@"VTConferenceStatusUpdated" object:nil];
}

- (void)initialize:(CDVInvokedUrlCommand *)command {
    if (_consumerKey == nil && _consumerSecret == nil) {
        _consumerKey = [command.arguments objectAtIndex:0];
        _consumerSecret = [command.arguments objectAtIndex:1];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [self initializeWithConsumerKey:self.consumerKey consumerSecret:self.consumerSecret];
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
        });
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
        });
    }
}

- (void)initializeWithConsumerKey:(NSString *)consumerKey consumerSecret:(NSString *)consumerSecret {
    [VoxeetSDK.shared initializeWithConsumerKey:consumerKey consumerSecret:consumerSecret];
    [VoxeetUXKit.shared initialize];
    
    VoxeetSDK.shared.notification.push.type = VTNotificationPushTypeCallKit;
    [VoxeetSDK.shared _registerComponentVersionWithName:kCordovaComponentName version:kCordovaPluginVersion];
}

- (void)initializeToken:(CDVInvokedUrlCommand *)command {
    NSString *accessToken = [command.arguments objectAtIndex:0];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared initializeWithAccessToken:accessToken refreshTokenClosureWithParam:^(void (^closure)(NSString *), BOOL isExpired) {
            self.refreshAccessTokenClosure = closure;
            
            if (self.refreshAccessTokenCommand != nil) {
                CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
                [pluginResult setKeepCallbackAsBool:YES];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:self.refreshAccessTokenCommand.callbackId];
            }
        }];
        [VoxeetUXKit.shared initialize];
        
        VoxeetSDK.shared.notification.push.type = VTNotificationPushTypeCallKit;
        [VoxeetSDK.shared _registerComponentVersionWithName:kCordovaComponentName version:kCordovaPluginVersion];
        
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)connect:(CDVInvokedUrlCommand *)command {
    NSDictionary *participant = [command.arguments objectAtIndex:0];
    NSString *externalID = [participant objectForKey:@"externalId"];
    NSString *name = [participant objectForKey:@"name"];
    NSString *avatarURL = [participant objectForKey:@"avatarUrl"];
    
    VTParticipantInfo *participantInfo = [[VTParticipantInfo alloc]
                                          initWithExternalID:![externalID isEqual:[NSNull null]] ? externalID : nil
                                          name:![name isEqual:[NSNull null]] ? name : nil
                                          avatarURL: ![avatarURL isEqual:[NSNull null]] ? avatarURL : nil];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.session openWithInfo:participantInfo completion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

- (void)disconnect:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.session closeWithCompletion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

- (void)create:(CDVInvokedUrlCommand *)command {
    NSDictionary<NSString *,id> *options = [command.arguments objectAtIndex:0];
    
    // Create conference options.
    VTConferenceOptions *conferenceOptions = [[VTConferenceOptions alloc] init];
    conferenceOptions.alias = [options valueForKey:@"alias"];
    conferenceOptions.pinCode = [options valueForKey:@"pinCode"];
    NSDictionary *params = [options valueForKey:@"params"];
    if (params) {
        conferenceOptions.params.liveRecording = [params valueForKey:@"liveRecording"];
        conferenceOptions.params.rtcpMode = [params valueForKey:@"rtcpMode"];
        conferenceOptions.params.stats = [params valueForKey:@"stats"];
        conferenceOptions.params.ttl = [params valueForKey:@"ttl"];
        conferenceOptions.params.videoCodec = [params valueForKey:@"videoCodec"];
        conferenceOptions.params.dolbyVoice = [params valueForKey:@"dolbyVoice"];
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference createWithOptions:conferenceOptions success:^(VTConference *conference) {
            NSDictionary *result = @{@"conferenceId": conference.id,
                                     @"conferenceAlias": conference.alias,
                                     @"isNew": [NSNumber numberWithBool:conference.isNew]};
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result] callbackId:command.callbackId];
        } fail:^(NSError *error) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
        }];
    });
}

- (void)join:(CDVInvokedUrlCommand *)command {
    NSString *conferenceID = [command.arguments objectAtIndex:0];
    BOOL isListener = NO;
    BOOL defaultVideo = VoxeetSDK.shared.conference.defaultVideo; /* Monkey patch with listener mode */
    if ([command.arguments count] > 1) {
        NSDictionary<NSString *, id> *options = [command.arguments objectAtIndex:1];
        NSDictionary *user = [options valueForKey:@"user"];
        if (user != nil) {
            NSString *type = [user valueForKey:@"type"];
            if (type != nil && [type isEqual:@"listener"]) {
                isListener = YES;
            }
        }
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference fetchWithConferenceID:conferenceID completion:^(VTConference *conference) {
            if (!isListener) {
                VTJoinOptions *options = [[VTJoinOptions alloc] init];
                options.constraints.video = VoxeetSDK.shared.conference.defaultVideo;
                [VoxeetSDK.shared.conference joinWithConference:conference options:options success:^(VTConference *conference2) {
                    NSDictionary *result = @{@"conferenceId": conference2.id, @"conferenceAlias": conference2.alias};
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result] callbackId:command.callbackId];
                } fail:^(NSError *error) {
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
                }];
            } else {
                VoxeetSDK.shared.conference.defaultVideo = NO;
                [VoxeetSDK.shared.conference listenWithConference:conference options:nil success:^(VTConference *conference2) {
                    VoxeetSDK.shared.conference.defaultVideo = defaultVideo;
                    NSDictionary *result = @{@"conferenceId": conference2.id, @"conferenceAlias": conference2.alias};
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result] callbackId:command.callbackId];
                } fail:^(NSError *error) {
                    VoxeetSDK.shared.conference.defaultVideo = defaultVideo;
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
                }];
            }
        }];
    });
}

- (void)leave:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference leaveWithCompletion:^(NSError *error) {
            if (error == nil || error.code == -10002) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

- (void)invite:(CDVInvokedUrlCommand *)command {
    NSString *conferenceID = [command.arguments objectAtIndex:0];
    NSArray *participants = [command.arguments objectAtIndex:1];
    NSMutableArray<VTParticipantInfo *> *participantInfos = [[NSMutableArray alloc] init];
    
    for (NSDictionary *participant in participants) {
        NSString *externalID = [participant objectForKey:@"externalId"];
        NSString *name = [participant objectForKey:@"name"];
        NSString *avatarURL = [participant objectForKey:@"avatarUrl"];
        
        VTParticipantInfo *participantInfo = [[VTParticipantInfo alloc]
                                              initWithExternalID:![externalID isEqual:[NSNull null]] ? externalID : nil
                                              name:![name isEqual:[NSNull null]] ? name : nil
                                              avatarURL: ![avatarURL isEqual:[NSNull null]] ? avatarURL : nil];
        [participantInfos addObject:participantInfo];
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference fetchWithConferenceID:conferenceID completion:^(VTConference *conference) {
            [VoxeetSDK.shared.notification inviteWithConference:conference participantInfos:participantInfos completion:^(NSError *error) {
                if (error == nil) {
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
                } else {
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
                }
            }];
        }];
    });
}

- (void)sendBroadcastMessage:(CDVInvokedUrlCommand *)command {
    NSString *message = [command.arguments objectAtIndex:0];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.command sendWithMessage:message completion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

- (void)appearMaximized:(CDVInvokedUrlCommand *)command {
    BOOL enabled = [[command.arguments objectAtIndex:0] boolValue];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        VTUXConferenceController *conferenceController = VoxeetUXKit.shared.conferenceController;
        if (conferenceController != nil) {
            conferenceController.appearMaximized = enabled;
        }
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)setUIConfiguration:(CDVInvokedUrlCommand *)command {
//    NSString *jsonStr = [command.arguments objectAtIndex:0];
//    NSError *jsonError;
//    NSData *jsonData = [jsonStr dataUsingEncoding:NSUTF8StringEncoding];
//    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:jsonData
//                                                         options:NSJSONReadingMutableContainers
//                                                           error:&jsonError];
}

- (void)defaultBuiltInSpeaker:(CDVInvokedUrlCommand *)command {
    BOOL enabled = [[command.arguments objectAtIndex:0] boolValue];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        VoxeetSDK.shared.conference.defaultBuiltInSpeaker = enabled;
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)defaultVideo:(CDVInvokedUrlCommand *)command {
    BOOL enabled = [[command.arguments objectAtIndex:0] boolValue];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        VoxeetSDK.shared.conference.defaultVideo = enabled;
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)setAudio3DEnabled:(CDVInvokedUrlCommand *)command {
    BOOL enabled = [[command.arguments objectAtIndex:0] boolValue];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        VoxeetSDK.shared.conference.audio3D = enabled;
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)setTelecomMode:(CDVInvokedUrlCommand *)command {
    BOOL enabled = [[command.arguments objectAtIndex:0] boolValue];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        VTUXConferenceController *conferenceController = VoxeetUXKit.shared.conferenceController;
        if (conferenceController != nil) {
            conferenceController.telecom = enabled;
        }
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)isAudio3DEnabled:(CDVInvokedUrlCommand *)command {
    BOOL isAudio3D = VoxeetSDK.shared.conference.audio3D;
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:isAudio3D] callbackId:command.callbackId];
}

- (void)isTelecomMode:(CDVInvokedUrlCommand *)command {
    BOOL isTelecom = NO;
    VTUXConferenceController *conferenceController = VoxeetUXKit.shared.conferenceController;
    if (conferenceController != nil) {
        isTelecom = conferenceController.telecom;
    }
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:isTelecom] callbackId:command.callbackId];
}

- (void)startVideo:(CDVInvokedUrlCommand *)command {
    //    BOOL isDefaultFrontFacing = [[command.arguments objectAtIndex:0] boolValue];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference startVideoWithParticipant:nil completion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

- (void)stopVideo:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference stopVideoWithParticipant:nil completion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

- (void)switchCamera:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.mediaDevice switchCameraWithCompletion:^{
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
        }];
    });
}

- (void)minimize:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetUXKit.shared.conferenceController minimize];
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)maximize:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetUXKit.shared.conferenceController maximize];
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)onConferenceStatusUpdatedEvent:(CDVInvokedUrlCommand *)command {
    // No need to be resolved because it's gonna be resolved in `conferenceStatusUpdated`.
    _conferenceStatusUpdatedCommand = command;
}

/*
 *  MARK: Recording
 */

- (void)startRecording:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.recording startWithFireInterval:0 completion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

- (void)stopRecording:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.recording stopWithCompletion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

/*
 *  MARK: Oauth2 helpers
 */

- (void)refreshAccessTokenCallback:(CDVInvokedUrlCommand *)command {
    // No need to be resolved because it's gonna be resolved in `initializeToken`.
    _refreshAccessTokenCommand = command;
}

- (void)onAccessTokenOk:(CDVInvokedUrlCommand *)command {
    NSString *accessToken = [command.arguments objectAtIndex:0];
    _refreshAccessTokenClosure(accessToken);
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void)onAccessTokenKo:(CDVInvokedUrlCommand *)command {
    _refreshAccessTokenClosure(nil);
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

/*
 *  MARK: Android compatibility methods
 */

- (void)broadcast:(CDVInvokedUrlCommand *)command {
    [self join:command];
}

- (void)screenAutoLock:(CDVInvokedUrlCommand *)command { /* Android compatibility */
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void)isUserLoggedIn:(CDVInvokedUrlCommand *)command { /* Android compatibility */
    BOOL isLogIn = (VoxeetSDK.shared.session.state == VTSessionStateConnected);
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:isLogIn] callbackId:command.callbackId];
}

- (void)checkForAwaitingConference:(CDVInvokedUrlCommand *)command { /* Android compatibility */
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

/*
 *  MARK: Observers
 */

- (void)conferenceStatusUpdated:(NSNotification *)notification {
    NSNumber *rawStatus = notification.userInfo[@"status"];
    VTConferenceStatus status = (VTConferenceStatus)rawStatus.intValue;
    NSString *statusStr = @"DEFAULT";
    
    switch (status) {
        case VTConferenceStatusCreating:
            statusStr = @"CREATING";
            break;
        case VTConferenceStatusCreated:
            statusStr = @"CREATED";
            break;
        case VTConferenceStatusJoining:
            statusStr = @"JOINING";
            break;
        case VTConferenceStatusJoined:
            statusStr = @"JOINED";
            break;
        case VTConferenceStatusLeaving:
            statusStr = @"LEAVING";
            break;
        case VTConferenceStatusLeft:
            statusStr = @"LEFT";
            break;
        case VTConferenceStatusEnded:
            statusStr = @"ENDED";
            break;
        case VTConferenceStatusDestroyed:
            statusStr = @"DESTROYED";
            break;
        case VTConferenceStatusError:
            statusStr = @"ERROR";
            break;
        default:
            break;
    }
    
    VTConference *conference = VoxeetSDK.shared.conference.current;
    if (conference != nil && _conferenceStatusUpdatedCommand != nil) {
        NSDictionary *statusDict = @{@"status": statusStr,
                                     @"conferenceId": conference.id,
                                     @"conferenceAlias": conference.alias};
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:statusDict];
        [pluginResult setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:_conferenceStatusUpdatedCommand.callbackId];
    }
}

/*
 *  MARK: Deprecated methods
 */

- (void)startConference:(CDVInvokedUrlCommand *)command { /* Deprecated */
    NSString *confAlias = [command.arguments objectAtIndex:0];
    NSArray *participants = [command.arguments objectAtIndex:1];
    NSMutableArray<VTParticipantInfo *> *participantInfos = [[NSMutableArray alloc] init];
    
    for (NSDictionary *participant in participants) {
        NSString *externalID = [participant objectForKey:@"externalId"];
        NSString *name = [participant objectForKey:@"name"];
        NSString *avatarURL = [participant objectForKey:@"avatarUrl"];
        
        VTParticipantInfo *participantInfo = [[VTParticipantInfo alloc]
                                              initWithExternalID:![externalID isEqual:[NSNull null]] ? externalID : nil
                                              name:![name isEqual:[NSNull null]] ? name : nil
                                              avatarURL: ![avatarURL isEqual:[NSNull null]] ? avatarURL : nil];
        [participantInfos addObject:participantInfo];
    }
    
    // Create conference options.
    VTConferenceOptions *conferenceOptions = [[VTConferenceOptions alloc] init];
    conferenceOptions.alias = confAlias;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference createWithOptions:conferenceOptions success:^(VTConference *conference) {
            VTJoinOptions *joinOptions = [[VTJoinOptions alloc] init];
            joinOptions.constraints.video = VoxeetSDK.shared.conference.defaultVideo;
            [VoxeetSDK.shared.conference joinWithConference:conference options:joinOptions success:^(VTConference *conference2) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } fail:^(NSError *error) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }];
            
            if (conference.isNew) {
                [VoxeetSDK.shared.notification inviteWithConference:conference participantInfos:participantInfos completion:nil];
            }
        } fail:^(NSError *error) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
        }];
    });
}

- (void)stopConference:(CDVInvokedUrlCommand *)command { /* Deprecated */
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference leaveWithCompletion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

- (void)openSession:(CDVInvokedUrlCommand *)command { /* Deprecated */
    [self connect:command];
}

- (void)closeSession:(CDVInvokedUrlCommand *)command { /* Deprecated */
    [self disconnect:command];
}

@end
