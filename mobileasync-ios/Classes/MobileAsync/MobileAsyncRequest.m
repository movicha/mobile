#import "MobileAsyncRequest.h"

@implementation MobileAsyncRequest

@synthesize successTarget;
@synthesize successAction;
@synthesize failureTarget;
@synthesize failureAction;
@synthesize userInfo;
@synthesize active;
@synthesize data;

- (id)init
{
    if (self = [super init])
    {
        data = [[NSMutableData alloc] init];
    }
    return self;
}

- (void)dealloc
{
    [userInfo release];
    [data release];
    [super dealloc];
}

@end
