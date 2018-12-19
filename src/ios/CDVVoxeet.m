#import "CDVVoxeet.h"
#import <Cordova/CDV.h>
#import <VoxeetSDK/VoxeetSDK.h>
#import <VoxeetConferenceKit/VoxeetConferenceKit.h>

@interface CDVVoxeet()

@property (nonatomic, copy) NSString *refreshAccessTokenID;
@property (nonatomic, copy) void (^refreshAccessTokenClosure)(NSString *);

@end

@implementation CDVVoxeet

- (void)initialize:(CDVInvokedUrlCommand *)command {
    NSString *consumerKey = [command.arguments objectAtIndex:0];
    NSString *consumerSecret = [command.arguments objectAtIndex:1];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        VoxeetSDK.shared.callKit = YES;
        
        [VoxeetSDK.shared initializeWithConsumerKey:consumerKey consumerSecret:consumerSecret userInfo:nil connectSession:true];
        [VoxeetConferenceKit.shared initialize];
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)initializeWithRefresh:(CDVInvokedUrlCommand *)command {
    NSString *accessToken = [command.arguments objectAtIndex:0];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        VoxeetSDK.shared.callKit = YES;
        
        [VoxeetSDK.shared initializeWithAccessToken:accessToken userInfo:nil refreshTokenClosure:^(void (^closure)(NSString *)) {
            self.refreshAccessTokenClosure = closure;
            
            CDVPluginResult *callBackRefresh = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [callBackRefresh setKeepCallbackAsBool:YES];
            [self.commandDelegate sendPluginResult:callBackRefresh callbackId:self.refreshAccessTokenID];
        }];
        [VoxeetConferenceKit.shared initialize];
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}

- (void)refreshAccessTokenCallback:(CDVInvokedUrlCommand *)command {
    self.refreshAccessTokenID = command.callbackId;
    /* No need to be resolved because it's gonna be resolved in `initializeWithRefresh` */
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
    NSDictionary<NSString *,id> *parameters = [command.arguments objectAtIndex:0];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference createWithParameters:parameters success:^(NSDictionary<NSString *,id> *response) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:response] callbackId:command.callbackId];
        } fail:^(NSError *error) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
        }];
    });
}

- (void)join:(CDVInvokedUrlCommand *)command {
    NSString *conferenceID = [command.arguments objectAtIndex:0];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        BOOL video = VoxeetSDK.shared.conference.defaultVideo;
        [VoxeetSDK.shared.conference joinWithConferenceID:conferenceID video:video userInfo:nil success:^(NSDictionary<NSString *,id> *response) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:response] callbackId:command.callbackId];
        } fail:^(NSError *error) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
        }];
    });
}

- (void)leave:(CDVInvokedUrlCommand *)command {
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
        VoxeetConferenceKit.shared.appearMaximized = enabled;
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
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

/*
 *  MARK: - Android compatibility methods
 */

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
 *  MARK: - Deprecated methods
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
