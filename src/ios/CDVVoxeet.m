#import "CDVVoxeet.h"
#import <Cordova/CDV.h>
#import <VoxeetSDK/VoxeetSDK.h>
#import <VoxeetConferenceKit/VoxeetConferenceKit.h>

@implementation CDVVoxeet
    
- (void)initialize:(CDVInvokedUrlCommand *)command {
    NSString *consumerKey = [command.arguments objectAtIndex:0];
    NSString *consumerSecret = [command.arguments objectAtIndex:1];
    
    VoxeetConferenceKit.shared.callKit = YES;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetConferenceKit.shared initializeWithConsumerKey:consumerKey consumerSecret:consumerSecret];
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    });
}
    
- (void)openSession:(CDVInvokedUrlCommand *)command {
    NSDictionary *participant = [command.arguments objectAtIndex:0];
    VTUser *user = [[VTUser alloc] initWithId:[participant objectForKey:@"externalId"] name:[participant objectForKey:@"name"] photoURL:[participant objectForKey:@"avatarUrl"]];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetConferenceKit.shared openSessionWithUser:user completion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}
    
- (void)updateSession:(CDVInvokedUrlCommand *)command {
    NSDictionary *participant = [command.arguments objectAtIndex:0];
    VTUser *user = [[VTUser alloc] initWithId:[participant objectForKey:@"externalId"] name:[participant objectForKey:@"name"] photoURL:[participant objectForKey:@"avatarUrl"]];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetConferenceKit.shared updateSessionWithUser:user completion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}
    
- (void)closeSession:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetConferenceKit.shared closeSessionWithCompletion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}
    
- (void)startConference:(CDVInvokedUrlCommand *)command {
    NSString *conferenceID = [command.arguments objectAtIndex:0];
    NSArray *participants = [command.arguments objectAtIndex:1];
    NSMutableArray *users = [[NSMutableArray alloc] init];
    
    for (NSDictionary *participant in participants) {
        VTUser *user = [[VTUser alloc] initWithId:[participant objectForKey:@"externalId"] name:[participant objectForKey:@"name"] photoURL:[participant objectForKey:@"avatarUrl"]];
        [users addObject:user];
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetConferenceKit.shared startConferenceWithId:conferenceID users:users invite:YES success:^(NSDictionary<NSString *,id> *response) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
        } fail:^(NSError *error) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
        }];
    });
}
    
- (void)stopConference:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetConferenceKit.shared stopConferenceWithCompletion:^(NSError *error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description] callbackId:command.callbackId];
            }
        }];
    });
}
    
- (void)appearMaximized:(CDVInvokedUrlCommand*)command {
    BOOL enabled = [[command.arguments objectAtIndex:0] boolValue];
    
    VoxeetConferenceKit.shared.appearMaximized = enabled;
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}
    
- (void)defaultBuiltInSpeaker:(CDVInvokedUrlCommand*)command {
    BOOL enabled = [[command.arguments objectAtIndex:0] boolValue];
    
    VoxeetConferenceKit.shared.defaultBuiltInSpeaker = enabled;
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}
    
- (void)screenAutoLock:(CDVInvokedUrlCommand*)command {
    BOOL enabled = [[command.arguments objectAtIndex:0] boolValue];
    
    VoxeetConferenceKit.shared.screenAutoLock = enabled;
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}
    
@end
