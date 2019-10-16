#import "CDVVoxeet.h"
#import <Cordova/CDV.h>
#import <VoxeetSDK/VoxeetSDK.h>
#import <VoxeetConferenceKit/VoxeetConferenceKit.h>

@interface CDVVoxeet()

@property (nonatomic, copy) NSString *consumerKey;
@property (nonatomic, copy) NSString *consumerSecret;
@property (nonatomic, copy) NSString *refreshAccessTokenID;
@property (nonatomic, copy) void (^refreshAccessTokenClosure)(NSString *);

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
    
    VoxeetSDK.shared.pushNotification.type = VTPushNotificationTypeCallKit;
}

- (void)initializeToken:(CDVInvokedUrlCommand *)command {
    NSString *accessToken = [command.arguments objectAtIndex:0];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared initializeWithAccessToken:accessToken refreshTokenClosure:^(void (^closure)(NSString *)) {
            self.refreshAccessTokenClosure = closure;
            
            CDVPluginResult *callBackRefresh = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [callBackRefresh setKeepCallbackAsBool:YES];
            [self.commandDelegate sendPluginResult:callBackRefresh callbackId:self.refreshAccessTokenID];
        }];
        [VoxeetUXKit.shared initialize];
        
        VoxeetSDK.shared.pushNotification.type = VTPushNotificationTypeCallKit;
        
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)connect:(CDVInvokedUrlCommand *)command {
    NSDictionary *participant = [command.arguments objectAtIndex:0];
    VTUser *user = [[VTUser alloc] initWithExternalID:[participant objectForKey:@"externalId"] name:[participant objectForKey:@"name"] avatarURL:[participant objectForKey:@"avatarUrl"]];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.session connectWithUser:user completion:^(NSError *error) {
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
        [VoxeetSDK.shared.session disconnectWithCompletion:^(NSError *error) {
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
    
    NSMutableDictionary *nativeOptions = [[NSMutableDictionary alloc] init];
    [nativeOptions setValue:[options valueForKey:@"alias"] forKey:@"conferenceAlias"];
    
    NSDictionary *params = [options valueForKey:@"params"];
    if (params) {
        NSMutableDictionary *nativeOptionsParams = [[NSMutableDictionary alloc] init];
        [nativeOptionsParams setValue:[params valueForKey:@"ttl"] forKey:@"ttl"];
        [nativeOptionsParams setValue:[params valueForKey:@"rtcpMode"] forKey:@"rtcpMode"];
        [nativeOptionsParams setValue:[params valueForKey:@"mode"] forKey:@"mode"];
        [nativeOptionsParams setValue:[params valueForKey:@"videoCodec"] forKey:@"videoCodec"];
        [nativeOptions setValue:nativeOptionsParams forKey:@"params"];
        
        if ([params valueForKey:@"liveRecording"]) {
            [nativeOptions setValue:@{@"liveRecording": [params valueForKey:@"liveRecording"]} forKey:@"metadata"];
        }
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference createWithParameters:nativeOptions success:^(NSDictionary<NSString *,id> *response) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:response] callbackId:command.callbackId];
        } fail:^(NSError *error) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
        }];
    });
}

- (void)join:(CDVInvokedUrlCommand *)command {
    NSString *conferenceID = [command.arguments objectAtIndex:0];
    NSDictionary<NSString *,id> *options = nil;
    if ([command.arguments count] > 1) {
        options = [command.arguments objectAtIndex:1];
    }
    
    NSMutableDictionary *nativeOptions = [[NSMutableDictionary alloc] init];
    [nativeOptions setValue:[options valueForKey:@"alias"] forKey:@"conferenceAlias"];
    
    NSDictionary *user = [options valueForKey:@"user"];
    if (user) {
        [nativeOptions setValue:[user valueForKey:@"type"] forKey:@"participantType"];
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        BOOL video = VoxeetSDK.shared.conference.defaultVideo;
        [VoxeetSDK.shared.conference joinWithConferenceID:conferenceID video:video userInfo:nativeOptions success:^(NSDictionary<NSString *,id> *response) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:response] callbackId:command.callbackId];
        } fail:^(NSError *error) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
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
    NSMutableArray *userIDs = [[NSMutableArray alloc] init];
    
    for (NSDictionary *participant in participants) {
        [userIDs addObject:[participant objectForKey:@"externalId"]];
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference inviteWithConferenceID:conferenceID externalIDs:userIDs completion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

- (void)sendBroadcastMessage:(CDVInvokedUrlCommand *)command {
    NSString *message = [command.arguments objectAtIndex:0];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference broadcastWithMessage:message completion:^(NSError *error) {
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
        VoxeetUXKit.shared.appearMaximized = enabled;
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)setUIConfiguration:(CDVInvokedUrlCommand *)command {
    NSString *jsonStr = [command.arguments objectAtIndex:0];
    NSError *jsonError;
    NSData *jsonData = [jsonStr dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:jsonData
                                                         options:NSJSONReadingMutableContainers
                                                           error:&jsonError];
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
        VoxeetUXKit.shared.telecom = enabled;
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)isAudio3DEnabled:(CDVInvokedUrlCommand *)command {
    BOOL isAudio3D = VoxeetSDK.shared.conference.audio3D;
    
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:isAudio3D] callbackId:command.callbackId];
}

- (void)isTelecomMode:(CDVInvokedUrlCommand *)command {
    BOOL isTelecom = VoxeetConferenceKit.shared.telecom;
    
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:isTelecom] callbackId:command.callbackId];
}

- (void)startVideo:(CDVInvokedUrlCommand *)command {
    //    BOOL isDefaultFrontFacing = [[command.arguments objectAtIndex:0] boolValue];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        //        [VoxeetSDK.shared.conference startVideoWithUserID:nil isDefaultFrontFacing:isDefaultFrontFacing completion:^(NSError *error) {
        [VoxeetSDK.shared.conference startVideoWithUserID:VoxeetSDK.shared.session.user.id completion:^(NSError *error) {
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
        //        [VoxeetSDK.shared.conference stopVideoWithUserID:nil completion:^(NSError *error) {
        [VoxeetSDK.shared.conference stopVideoWithUserID:VoxeetSDK.shared.session.user.id completion:^(NSError *error) {
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
        [VoxeetSDK.shared.conference switchCameraWithCompletion:^{
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
        }];
    });
}

/*
 *  MARK: Recording
 */

- (void)startRecording:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference startRecordingWithFireInterval:0 completion:^(NSError *error) {
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
        [VoxeetSDK.shared.conference stopRecordingWithCompletion:^(NSError *error) {
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
    self.refreshAccessTokenID = command.callbackId;
    // No need to be resolved because it's gonna be resolved in `initializeToken`
}

- (void)onAccessTokenOk:(CDVInvokedUrlCommand *)command {
    NSString *accessToken = [command.arguments objectAtIndex:0];
    self.refreshAccessTokenClosure(accessToken);
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void)onAccessTokenKo:(CDVInvokedUrlCommand *)command {
    self.refreshAccessTokenClosure(nil);
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
 *  MARK: Deprecated methods
 */

- (void)startConference:(CDVInvokedUrlCommand *)command { /* Deprecated */
    NSString *confAlias = [command.arguments objectAtIndex:0];
    NSArray *participants = [command.arguments objectAtIndex:1];
    NSMutableArray *userIDs = [[NSMutableArray alloc] init];
    
    for (NSDictionary *participant in participants) {
        [userIDs addObject:[participant objectForKey:@"externalId"]];
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference createWithParameters:@{@"conferenceAlias": confAlias} success:^(NSDictionary<NSString *,id> *response) {
            NSString *confID = response[@"conferenceId"];
            BOOL isNew = response[@"isNew"];
            BOOL video = VoxeetSDK.shared.conference.defaultVideo;
            
            [VoxeetSDK.shared.conference joinWithConferenceID:confID video:video userInfo:nil success:^(NSDictionary<NSString *,id> *response) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } fail:^(NSError *error) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }];
            
            if (isNew) {
                [VoxeetSDK.shared.conference inviteWithConferenceID:confID externalIDs:userIDs completion:^(NSError *error) {}];
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
    NSDictionary *participant = [command.arguments objectAtIndex:0];
    VTUser *user = [[VTUser alloc] initWithExternalID:[participant objectForKey:@"externalId"] name:[participant objectForKey:@"name"] avatarURL:[participant objectForKey:@"avatarUrl"]];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.session connectWithUser:user completion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

- (void)closeSession:(CDVInvokedUrlCommand *)command { /* Deprecated */
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.session disconnectWithCompletion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}

@end
