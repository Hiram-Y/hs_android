package com.my.android.view;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.animation.Interpolator;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.attention.BounceAnimator;
import com.daimajia.androidanimations.library.attention.FlashAnimator;
import com.daimajia.androidanimations.library.attention.PulseAnimator;
import com.daimajia.androidanimations.library.attention.RubberBandAnimator;
import com.daimajia.androidanimations.library.attention.ShakeAnimator;
import com.daimajia.androidanimations.library.attention.StandUpAnimator;
import com.daimajia.androidanimations.library.attention.SwingAnimator;
import com.daimajia.androidanimations.library.attention.TadaAnimator;
import com.daimajia.androidanimations.library.attention.WaveAnimator;
import com.daimajia.androidanimations.library.attention.WobbleAnimator;
import com.daimajia.androidanimations.library.bouncing_entrances.BounceInAnimator;
import com.daimajia.androidanimations.library.bouncing_entrances.BounceInDownAnimator;
import com.daimajia.androidanimations.library.bouncing_entrances.BounceInLeftAnimator;
import com.daimajia.androidanimations.library.bouncing_entrances.BounceInRightAnimator;
import com.daimajia.androidanimations.library.bouncing_entrances.BounceInUpAnimator;
import com.daimajia.androidanimations.library.fading_entrances.FadeInAnimator;
import com.daimajia.androidanimations.library.fading_entrances.FadeInDownAnimator;
import com.daimajia.androidanimations.library.fading_entrances.FadeInLeftAnimator;
import com.daimajia.androidanimations.library.fading_entrances.FadeInRightAnimator;
import com.daimajia.androidanimations.library.fading_entrances.FadeInUpAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutDownAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutLeftAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutRightAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutUpAnimator;
import com.daimajia.androidanimations.library.flippers.FlipInXAnimator;
import com.daimajia.androidanimations.library.flippers.FlipOutXAnimator;
import com.daimajia.androidanimations.library.flippers.FlipOutYAnimator;
import com.daimajia.androidanimations.library.rotating_entrances.RotateInAnimator;
import com.daimajia.androidanimations.library.rotating_entrances.RotateInDownLeftAnimator;
import com.daimajia.androidanimations.library.rotating_entrances.RotateInDownRightAnimator;
import com.daimajia.androidanimations.library.rotating_entrances.RotateInUpLeftAnimator;
import com.daimajia.androidanimations.library.rotating_entrances.RotateInUpRightAnimator;
import com.daimajia.androidanimations.library.rotating_exits.RotateOutAnimator;
import com.daimajia.androidanimations.library.rotating_exits.RotateOutDownLeftAnimator;
import com.daimajia.androidanimations.library.rotating_exits.RotateOutDownRightAnimator;
import com.daimajia.androidanimations.library.rotating_exits.RotateOutUpLeftAnimator;
import com.daimajia.androidanimations.library.rotating_exits.RotateOutUpRightAnimator;
import com.daimajia.androidanimations.library.sliders.SlideInDownAnimator;
import com.daimajia.androidanimations.library.sliders.SlideInLeftAnimator;
import com.daimajia.androidanimations.library.sliders.SlideInRightAnimator;
import com.daimajia.androidanimations.library.sliders.SlideInUpAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutDownAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutLeftAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutRightAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutUpAnimator;
import com.daimajia.androidanimations.library.specials.HingeAnimator;
import com.daimajia.androidanimations.library.specials.RollInAnimator;
import com.daimajia.androidanimations.library.specials.RollOutAnimator;
import com.daimajia.androidanimations.library.specials.in.DropOutAnimator;
import com.daimajia.androidanimations.library.specials.in.LandingAnimator;
import com.daimajia.androidanimations.library.specials.out.TakingOffAnimator;
import com.daimajia.androidanimations.library.zooming_entrances.ZoomInAnimator;
import com.daimajia.androidanimations.library.zooming_entrances.ZoomInDownAnimator;
import com.daimajia.androidanimations.library.zooming_entrances.ZoomInLeftAnimator;
import com.daimajia.androidanimations.library.zooming_entrances.ZoomInRightAnimator;
import com.daimajia.androidanimations.library.zooming_entrances.ZoomInUpAnimator;
import com.daimajia.androidanimations.library.zooming_exits.ZoomOutAnimator;
import com.daimajia.androidanimations.library.zooming_exits.ZoomOutDownAnimator;
import com.daimajia.androidanimations.library.zooming_exits.ZoomOutLeftAnimator;
import com.daimajia.androidanimations.library.zooming_exits.ZoomOutRightAnimator;
import com.daimajia.androidanimations.library.zooming_exits.ZoomOutUpAnimator;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;

/**
 * 动画操作封装类，
 * <p><a href="https://camo.githubusercontent.com/c41223966bdfed2260dbbabbcbae648e5db542c6/687474703a2f2f7777332e73696e61696d672e636e2f6d773639302f3631306463303334677731656a37356d69327737376732306333306a623471722e676966">动画效果演示图</a>
 * <p>使用开源库封装，相关库打成了一个android-animations.jar
 * <p><a href="https://github.com/daimajia/AndroidViewAnimations">开源项目链接地址</a>
 * 
 * @author hushuai
 */
public class MyAnimator {
	private static final long DURATION = BaseViewAnimator.DURATION;
	private static final long NO_DELAY = 0;

	private AnimType animType;
	private long duration = DURATION;
	private long delay = NO_DELAY;
	private Interpolator interpolator;
	private List<MyAnimatorListener> callbacks = new ArrayList<MyAnimatorListener>();;
	private View target;
	private BaseViewAnimator animator;

	private MyAnimator(AnimType animType) {
		this.animType = animType;
	}
	/**
	 * 用给定的动画类型创建一个动画操作类
	 * @param animType 动画的类型  {@link AnimType}
	 * @return
	 */
	public static MyAnimator newInstance(AnimType animType) {
		return new MyAnimator(animType);
	}
	
	/**
	 * 设置动画持续的时间
	 * @param duration
	 * @return
	 */
	public MyAnimator setDuration(long duration) {
		this.duration = duration;
		return this;
	}

	/**
	 * 设置动画开始的延时时间
	 * @param delay
	 * @return
	 */
	public MyAnimator setDelay(long delay) {
		this.delay = delay;
		return this;
	}

	/**
	 * 设置动画的加速度器
	 * @param interpolator
	 * @return
	 */
	public MyAnimator setInterpolate(Interpolator interpolator) {
		this.interpolator = interpolator;
		return this;
	}
	/**
	 * 设置动画监听
	 * @param listener
	 * @return
	 */
	public MyAnimator setListener(MyAnimatorListener listener) {
		callbacks.add(listener);
		return this;
	}

	/**
	 * 设置播放动画的视图，即开始播放动画
	 * @param target 显示动画的控件
	 * @return
	 */
	public void playOn(View target) {
		this.target = target;
		animator = play();
	}

	public boolean isStarted() {
		return animator.isStarted();
	}

	public boolean isRunning() {
		return animator.isRunning();
	}

	public void stop(boolean reset) {
		animator.cancel();
		if (reset)
			animator.reset(target);
	}

	private BaseViewAnimator play() {
		final BaseViewAnimator animator = animType.getAnimator();
		animator.setDuration(duration).setInterpolator(interpolator).setStartDelay(delay);
		
		if (callbacks.size() > 0) {
			for (MyAnimatorListener callback : callbacks) {
				animator.addAnimatorListener(new Listener(callback));
			}
		}

		animator.animate(target);
		return animator;
	}
	
	private class Listener implements AnimatorListener{
		private MyAnimatorListener mlistener;
		
		public Listener(MyAnimatorListener mlistener) {
			this.mlistener = mlistener;
		}

		@Override
		public void onAnimationStart(Animator animation) {
			mlistener.onAnimationStart();
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			mlistener.onAnimationEnd();
		}

		@Override
		public void onAnimationCancel(Animator animation) {}
		@Override
		public void onAnimationRepeat(Animator animation) {}
	}
	
	/**动画监听*/
	public interface MyAnimatorListener{
		public void onAnimationStart();
		public void onAnimationEnd();
	}
	
	/**
	 * 动画样式
	 */
	public enum AnimType {

		DropOut(DropOutAnimator.class),
		Landing(LandingAnimator.class),
		TakingOff(TakingOffAnimator.class),
		Flash(FlashAnimator.class),
		Pulse(PulseAnimator.class),
		RubberBand(RubberBandAnimator.class),
		Shake(ShakeAnimator.class),
		Swing(SwingAnimator.class),
		Wobble(WobbleAnimator.class),
		Bounce(BounceAnimator.class),
		Tada(TadaAnimator.class),
		StandUp(StandUpAnimator.class),
		Wave(WaveAnimator.class),
		Hinge(HingeAnimator.class),
		RollIn(RollInAnimator.class),
		RollOut(RollOutAnimator.class),
		BounceIn(BounceInAnimator.class),
		BounceInDown(BounceInDownAnimator.class),
		BounceInLeft(BounceInLeftAnimator.class),
		BounceInRight(BounceInRightAnimator.class),
		BounceInUp(BounceInUpAnimator.class),
		FadeIn(FadeInAnimator.class),
		FadeInUp(FadeInUpAnimator.class),
		FadeInDown(FadeInDownAnimator.class),
		FadeInLeft(FadeInLeftAnimator.class),
		FadeInRight(FadeInRightAnimator.class),
		FadeOut(FadeOutAnimator.class),
		FadeOutDown(FadeOutDownAnimator.class),
		FadeOutLeft(FadeOutLeftAnimator.class),
		FadeOutRight(FadeOutRightAnimator.class),
		FadeOutUp(FadeOutUpAnimator.class),
		FlipInX(FlipInXAnimator.class),
		FlipOutX(FlipOutXAnimator.class),
		FlipOutY(FlipOutYAnimator.class),
		RotateIn(RotateInAnimator.class),
		RotateInDownLeft(RotateInDownLeftAnimator.class),
		RotateInDownRight(RotateInDownRightAnimator.class),
		RotateInUpLeft(RotateInUpLeftAnimator.class),
		RotateInUpRight(RotateInUpRightAnimator.class),
		RotateOut(RotateOutAnimator.class),
		RotateOutDownLeft(RotateOutDownLeftAnimator.class),
		RotateOutDownRight(RotateOutDownRightAnimator.class),
		RotateOutUpLeft(RotateOutUpLeftAnimator.class),
		RotateOutUpRight(RotateOutUpRightAnimator.class),
		SlideInLeft(SlideInLeftAnimator.class),
		SlideInRight(SlideInRightAnimator.class),
		SlideInUp(SlideInUpAnimator.class),
		SlideInDown(SlideInDownAnimator.class),
		SlideOutLeft(SlideOutLeftAnimator.class),
		SlideOutRight(SlideOutRightAnimator.class),
		SlideOutUp(SlideOutUpAnimator.class),
		SlideOutDown(SlideOutDownAnimator.class),
		ZoomIn(ZoomInAnimator.class),
		ZoomInDown(ZoomInDownAnimator.class),
		ZoomInLeft(ZoomInLeftAnimator.class),
		ZoomInRight(ZoomInRightAnimator.class),
		ZoomInUp(ZoomInUpAnimator.class),
		ZoomOut(ZoomOutAnimator.class), 
		ZoomOutDown(ZoomOutDownAnimator.class), 
		ZoomOutLeft(ZoomOutLeftAnimator.class), 
		ZoomOutRight(ZoomOutRightAnimator.class), 
		ZoomOutUp(ZoomOutUpAnimator.class);

		private Class<?> animatorClazz;

		private AnimType(Class<?> clazz) {
			animatorClazz = clazz;
		}

		public BaseViewAnimator getAnimator() {
			try {
				return (BaseViewAnimator) animatorClazz.newInstance();
			} catch (Exception e) {
				throw new Error("Can not init animatorClazz instance");
			}
		}
	}
}
