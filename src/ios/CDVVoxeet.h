#import <Cordova/CDV.h>

@interface CDVVoxeet: CDVPlugin

- (void)initialize:(CDVInvokedUrlCommand *)command;
- (void)initializeToken:(CDVInvokedUrlCommand *)command;
- (void)connect:(CDVInvokedUrlCommand *)command;
- (void)disconnect:(CDVInvokedUrlCommand *)command;
- (void)create:(CDVInvokedUrlCommand *)command;
- (void)join:(CDVInvokedUrlCommand *)command;
- (void)leave:(CDVInvokedUrlCommand *)command;
- (void)invite:(CDVInvokedUrlCommand *)command;
- (void)sendBroadcastMessage:(CDVInvokedUrlCommand *)command;
- (void)appearMaximized:(CDVInvokedUrlCommand *)command;
- (void)setUIConfiguration:(CDVInvokedUrlCommand *)command;
- (void)defaultBuiltInSpeaker:(CDVInvokedUrlCommand *)command;
- (void)defaultVideo:(CDVInvokedUrlCommand *)command;
- (void)setAudio3DEnabled:(CDVInvokedUrlCommand *)command;
- (void)setTelecomMode:(CDVInvokedUrlCommand *)command;
- (void)isAudio3DEnabled:(CDVInvokedUrlCommand *)command;
- (void)isTelecomMode:(CDVInvokedUrlCommand *)command;
- (void)startVideo:(CDVInvokedUrlCommand *)command;
- (void)stopVideo:(CDVInvokedUrlCommand *)command;
- (void)switchCamera:(CDVInvokedUrlCommand *)command;

/* MARK: Recording */

- (void)startRecording:(CDVInvokedUrlCommand *)command;
- (void)stopRecording:(CDVInvokedUrlCommand *)command;

/* MARK: Oauth2 helpers */

- (void)refreshAccessTokenCallback:(CDVInvokedUrlCommand *)command;
- (void)onAccessTokenOk:(CDVInvokedUrlCommand *)command;
- (void)onAccessTokenKo:(CDVInvokedUrlCommand *)command;

/* MARK: Android compatibility methods */

- (void)broadcast:(CDVInvokedUrlCommand *)command;
- (void)screenAutoLock:(CDVInvokedUrlCommand *)command;
- (void)isUserLoggedIn:(CDVInvokedUrlCommand *)command;
- (void)checkForAwaitingConference:(CDVInvokedUrlCommand *)command;

/* MARK: Deprecated methods */

- (void)startConference:(CDVInvokedUrlCommand *)command;
- (void)stopConference:(CDVInvokedUrlCommand *)command;

@end
