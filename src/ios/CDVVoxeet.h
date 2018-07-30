#import <Cordova/CDV.h>

@interface CDVVoxeet : CDVPlugin
    
- (void)initialize:(CDVInvokedUrlCommand*)command;
- (void)openSession:(CDVInvokedUrlCommand*)command;
- (void)updateSession:(CDVInvokedUrlCommand*)command;
- (void)closeSession:(CDVInvokedUrlCommand*)command;
- (void)startConference:(CDVInvokedUrlCommand*)command;
- (void)stopConference:(CDVInvokedUrlCommand*)command;
- (void)appearMaximized:(CDVInvokedUrlCommand*)command;
- (void)defaultBuiltInSpeaker:(CDVInvokedUrlCommand*)command;
- (void)screenAutoLock:(CDVInvokedUrlCommand*)command;
- (void)sendBroadcastMessage:(CDVInvokedUrlCommand*)command;

@end
