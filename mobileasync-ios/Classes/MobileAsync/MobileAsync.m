#import "MobileAsync.h"
#import "MobileAsyncRequest.h"

@implementation MobileAsync

@synthesize concurrentRequestsLimit;

+ (MobileAsync *)instance
{
    static MobileAsync *x = nil;
    if (x == nil)
        x = [[MobileAsync alloc] init];
    return x;
}

- (id)init
{
    if (self = [super init])
    {
        concurrentRequestsLimit = 3;
        requests = [[NSMutableDictionary alloc] init];
        queue = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)dealloc
{
    [self cancelAllRequests];
    
    [requests release];
    [queue release];
    [super dealloc];
}

- (void)startRequest:(NSURLConnection *)con
{
    MobileAsyncRequest *request = [requests objectForKey:
                                [NSValue valueWithNonretainedObject:con]];
    request.active = YES;
    ++activeRequestsCount;
    [con start];
}

- (void)stopRequest:(NSURLConnection *)con
{
    MobileAsyncRequest *request = [requests objectForKey:
                                [NSValue valueWithNonretainedObject:con]];
    request.active = NO;
    --activeRequestsCount;
    [con cancel];
}

- (void)queueRequest:(NSURLConnection *)con
{
    [queue addObject:con];
}

- (NSURLConnection *)dequeueRequest
{
    NSURLConnection *con = [[queue objectAtIndex:0] retain];
    [queue removeObjectAtIndex:0];
    return [con autorelease];
}

- (void)connectionEnded
{
    if (activeRequestsCount < concurrentRequestsLimit && [queue count] > 0)
        [self startRequest:[self dequeueRequest]];
}

#pragma mark NSURLConnection delegate

- (void)connection:(NSURLConnection *)con
    didReceiveResponse:(NSURLResponse *)response
{
    MobileAsyncRequest *request = [requests objectForKey:
                                [NSValue valueWithNonretainedObject:con]];
    [request.data setLength:0];
}

- (void)connection:(NSURLConnection *)con didReceiveData:(NSData *)data
{
    MobileAsyncRequest *request = [requests objectForKey:
                                [NSValue valueWithNonretainedObject:con]];
    [request.data appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)con
{
    MobileAsyncRequest *request = [requests objectForKey:
                                [NSValue valueWithNonretainedObject:con]];
    request.active = NO;
    --activeRequestsCount;
    [request.successTarget performSelector:request.successAction
     withObject:request.data withObject:request.userInfo];
    [requests removeObjectForKey:[NSValue valueWithNonretainedObject:con]];
    [self connectionEnded];
}

- (void)connection:(NSURLConnection *)con
    didFailWithError:(NSError *)err
{
    MobileAsyncRequest *request = [requests objectForKey:
                                [NSValue valueWithNonretainedObject:con]];
    request.active = NO;
    --activeRequestsCount;
    if (request.failureTarget != nil)
        [request.failureTarget performSelector:request.successAction
         withObject:err withObject:request.userInfo];
    [requests removeObjectForKey:[NSValue valueWithNonretainedObject:con]];
    [self connectionEnded];
}

#pragma mark Public

- (NSURLConnection *)addRequest:(NSURLRequest *)req
    successTarget:(id)successTarget successAction:(SEL)successAction
    failureTarget:(id)failureTarget failureAction:(SEL)failureAction
    userInfo:(NSDictionary *)userInfo
{
    NSURLConnection *con = [[NSURLConnection alloc] initWithRequest:req
                            delegate:self startImmediately:NO];
    [con scheduleInRunLoop:[NSRunLoop currentRunLoop]
     forMode:NSDefaultRunLoopMode];
    if (con == nil)
        return nil;
    MobileAsyncRequest *request = [[MobileAsyncRequest alloc] init];
    request.successTarget = successTarget;
    request.successAction = successAction;
    request.failureTarget = failureTarget;
    request.failureAction = failureAction;
    request.userInfo = userInfo;
    [requests setObject:request forKey:
     [NSValue valueWithNonretainedObject:con]];
    [request release];
    if (activeRequestsCount < concurrentRequestsLimit)
        [self startRequest:con];
    else
        [self queueRequest:con];
    return [con autorelease];
}

- (NSURLConnection *)addRequestForUrl:(NSString *)url
    successTarget:(id)successTarget successAction:(SEL)successAction
    failureTarget:(id)failureTarget failureAction:(SEL)failureAction
    userInfo:(NSDictionary *)userInfo
{
    return [self addRequest:[NSURLRequest requestWithURL:
                             [NSURL URLWithString:url]]
            successTarget:successTarget successAction:successAction
            failureTarget:failureTarget failureAction:failureAction
            userInfo:userInfo];
}

- (void)cancelRequest:(NSURLConnection *)con
{
    MobileAsyncRequest *request = [requests objectForKey:
                                [NSValue valueWithNonretainedObject:con]];
    if (request == nil)
        return;
    if (request.active)
        [self stopRequest:con];
    else
        [queue removeObject:con];
    [requests removeObjectForKey:[NSValue valueWithNonretainedObject:con]];
}

- (void)cancelAllRequests
{
    for (NSURLConnection *con in requests)
    {
        MobileAsyncRequest *request = [requests objectForKey:
                                    [NSValue valueWithNonretainedObject:con]];
        if (request.active)
            [self stopRequest:con];
    }
    [requests removeAllObjects];
    [queue removeAllObjects];
}

@end
