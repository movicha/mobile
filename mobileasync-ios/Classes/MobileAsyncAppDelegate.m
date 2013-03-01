#import "MobileAsyncAppDelegate.h"
#import "MobileAsync.h"

@implementation MobileAsyncAppDelegate

- (void)applicationDidFinishLaunching:(UIApplication *)application
{
    [[MobileAsync instance] addRequestForUrl:@"http://google.com/"
     successTarget:self successAction:@selector(fetchedWebpage:userInfo:)
     failureTarget:nil failureAction:nil
     userInfo:[NSDictionary dictionaryWithObject:@"google.com" forKey:@"url"]];
    [[MobileAsync instance] addRequestForUrl:@"http://apple.com/"
     successTarget:self successAction:@selector(fetchedWebpage:userInfo:)
     failureTarget:nil failureAction:nil
     userInfo:[NSDictionary dictionaryWithObject:@"apple.com" forKey:@"url"]];
    [[MobileAsync instance] addRequestForUrl:@"http://nyt.com/"
     successTarget:self successAction:@selector(fetchedWebpage:userInfo:)
     failureTarget:nil failureAction:nil
     userInfo:[NSDictionary dictionaryWithObject:@"nyt.com" forKey:@"url"]];
    [[MobileAsync instance] addRequestForUrl:@"http://bbc.com/"
     successTarget:self successAction:@selector(fetchedWebpage:userInfo:)
     failureTarget:nil failureAction:nil
     userInfo:[NSDictionary dictionaryWithObject:@"bbc.com" forKey:@"url"]];
    [[MobileAsync instance] addRequestForUrl:@"http://android.com/"
     successTarget:self successAction:@selector(fetchedWebpage:userInfo:)
     failureTarget:nil failureAction:nil
     userInfo:[NSDictionary dictionaryWithObject:@"android.com" forKey:@"url"]];
}

- (void)dealloc
{
    [[MobileAsync instance] release]; // release singleton
    [super dealloc];
}

- (void)fetchedWebpage:(NSData *)data userInfo:(NSDictionary *)userInfo
{
    NSString *url = [userInfo objectForKey:@"url"];
    NSLog(@"Fetched page: %@", url);
}

@end
