package es.iessaladillo.pedrojoya.pr224;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

@SuppressWarnings("WeakerAccess")
public class ScrollAwareFabBehavior extends FloatingActionButton.Behavior {

    private static final long ANIMATION_DURATION = 200;

    private boolean isHidden;

    @SuppressWarnings("unused")
    public ScrollAwareFabBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull final CoordinatorLayout coordinatorLayout,
            @NonNull final FloatingActionButton child, @NonNull final View directTargetChild, @NonNull final View target,
            final int nestedScrollAxes, int type) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
                coordinatorLayout, child, directTargetChild, target, nestedScrollAxes, type);
    }


    @Override
    public void onNestedScroll(@NonNull final CoordinatorLayout coordinatorLayout,
            @NonNull final FloatingActionButton child, @NonNull final View target, final int dxConsumed,
            final int dyConsumed, final int dxUnconsumed, final int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed, type);
        if (dyConsumed > 0 && !isHidden) {
            child.animate().scaleX(0).scaleY(0).rotation(-90).setDuration(ANIMATION_DURATION)
                    .setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            isHidden = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

        } else if (dyConsumed < 0 && isHidden) {
            child.animate().scaleX(1).scaleY(1).rotation(0).setDuration(ANIMATION_DURATION)
                    .setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            isHidden = false;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    }

}