int main(int argc, char *argv[])
{    
    NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
    int retVal = UIApplicationMain(argc, argv, nil, @"MobileAsyncAppDelegate");
    [pool release];
    return retVal;
}
