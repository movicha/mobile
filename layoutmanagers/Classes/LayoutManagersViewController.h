@class VLayoutView;
@class HLayoutView;

@interface LayoutManagersViewController : UIViewController
{
    VLayoutView *mainLayout;
    VLayoutView *scrollingExampleLayout;
    UILabel *scrollingExampleLastLabel;
}

- (void)sizingAndMarginsExample;
- (void)scrollingExample;
- (void)alignmentExample;

@end
