package com.cdbwsoft.library.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.R;

import java.lang.ref.WeakReference;

/**
 * 对话框
 * Created by DDL on 2016/5/30.
 */
public class AlertDialog extends Dialog {
	private Button mButtonNegative;
	private CharSequence mButtonNegativeText;
	private Button mButtonNeutral;
	private CharSequence mButtonNeutralText;
	private Button mButtonPositive;
	private CharSequence mButtonPositiveText;
	private ButtonHandler mHandler;
	private Drawable mIcon;
	private int mIconId;
	private ImageView mIconView;
	private CharSequence    mMessage;
	private TextView        mMessageView;
	private OnClickListener mNegativeButtonListener;
	private OnClickListener mNeutralButtonListener;
	private OnClickListener mPositiveButtonListener;
	private ScrollView      mScrollView;
	private CharSequence    mTitle;
	public View mCustomTitleView;
	private TextView mTitleView;
	private View            mView;
	private int             mViewLayoutResId;
	private ListView        mListView;
	private Message mButtonPositiveMessage;
	private Message mButtonNegativeMessage;
	private Message mButtonNeutralMessage;
	private boolean initialized;
	private boolean mReversal = AppConfig.DIALOG_BUTTON_REVERSAL;
	View.OnClickListener mButtonHandler = new View.OnClickListener() {
		public void onClick(View v) {
			Message m = null;
			if (v == mButtonPositive && mButtonPositiveMessage != null) {
				m = Message.obtain(mButtonPositiveMessage);
			} else if (v == mButtonNegative && mButtonNegativeMessage != null) {
				m = Message.obtain(mButtonNegativeMessage);
			} else if (v == mButtonNeutral && mButtonNeutralMessage != null) {
				m = Message.obtain(mButtonNeutralMessage);
			}
			if (m != null) {
				m.sendToTarget();
			}
			// Post a message so we dismiss after the above handlers are executed
			mHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG, AlertDialog.this).sendToTarget();
		}
	};

	public AlertDialog(Context context) {
		super(context);
	}

	public AlertDialog(Context context, int themeResId) {
		super(context, themeResId);
	}

	protected AlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public void setReversal(boolean reversal){
		if(mReversal != reversal) {
			mReversal = reversal;
			setupButtons();
		}
	}
	static boolean canTextInput(View v) {
		if (v.onCheckIsTextEditor()) {
			return true;
		}

		if (!(v instanceof ViewGroup)) {
			return false;
		}

		ViewGroup vg = (ViewGroup)v;
		int i = vg.getChildCount();
		while (i > 0) {
			i--;
			v = vg.getChildAt(i);
			if (canTextInput(v)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialized = true;
		mHandler = new ButtonHandler(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_layout);


		LinearLayout contentPanel = (LinearLayout) findViewById(R.id.contentPanel);
		setupContent(contentPanel);

		boolean hasButtons = setupButtons();

		LinearLayout topPanel = (LinearLayout) findViewById(R.id.topPanel);
		boolean hasTitle = setupTitle(topPanel);

		View buttonPanel = findViewById(R.id.buttonPanel);
		if (!hasButtons) {
			buttonPanel.setVisibility(View.GONE);
			setCanceledOnTouchOutside(true);
		}

		FrameLayout customPanel = (FrameLayout) findViewById(R.id.customPanel);

		View customView;
		if (mView != null) {
			customView = mView;
		} else if (mViewLayoutResId != 0) {
			customView = getLayoutInflater().inflate(mViewLayoutResId, customPanel, false);
		} else {
			customView = null;
		}
		boolean hasCustomView = customView != null;
		if (!hasCustomView || !canTextInput(customView)) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
			                 WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		}
		if (hasCustomView) {
			FrameLayout custom = (FrameLayout) findViewById(R.id.custom);
			custom.addView(customView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

			if (mListView != null) {
				((LinearLayout.LayoutParams) customPanel.getLayoutParams()).weight = 0;
			}
		} else {
			findViewById(R.id.customPanel).setVisibility(View.GONE);
		}

        /* Only display the divider if we have a title and a
         * custom view or a message.
         */
		if (hasTitle) {
			View divider = null;
			if (mMessage != null || mView != null || mListView != null) {
				divider = findViewById(R.id.titleDivider);
			}

			if (divider != null) {
				divider.setVisibility(View.VISIBLE);
			}
		}
	}
	public void setTitle(int titleId) {
		mTitle = getContext().getText(titleId);
	}

	public void setTitle(CharSequence title) {
		mTitle = title;
	}

	public void setMessage(CharSequence message) {
		mMessage = message;
	}

	public void setMessage(int messageId) {
		setMessage(getContext().getString(messageId));
	}

	public void setPositiveButton(CharSequence text, OnClickListener listener) {
		mButtonPositiveText = text;
		mPositiveButtonListener = listener;
		setupButtons();
	}

	public void setPositiveButton(int textId, OnClickListener listener) {
		setPositiveButton(getContext().getString(textId), listener);
	}

	public void setNegativeButton(CharSequence text, OnClickListener listener) {
		mButtonNegativeText = text;
		mNegativeButtonListener = listener;
		setupButtons();
	}

	public void setNegativeButton(int textId, OnClickListener listener) {
		setNegativeButton(getContext().getString(textId), listener);
	}

	public void setNeutralButton(CharSequence text, final OnClickListener listener) {
		mButtonNeutralText = text;
		mNeutralButtonListener = listener;
		setupButtons();
	}

	public void setNeutralButton(int textId, final OnClickListener listener) {
		setNeutralButton(getContext().getString(textId), listener);
	}

	public void setView(int layoutResId) {
		mView = null;
		mViewLayoutResId = layoutResId;
	}
	public void setView(View view) {
		mView = view;
		mViewLayoutResId = 0;
	}

	public void setIcon(int iconId) {
		mIconId = iconId;
	}

	public void setIcon(Drawable icon) {
		mIcon = icon;
	}

	private void setupContent(LinearLayout contentPanel) {
		mScrollView = (ScrollView) findViewById(R.id.scrollView);
		mScrollView.setFocusable(false);

		// Special case for users that only want to display a String
		mMessageView = (TextView) findViewById(R.id.message);
		if (mMessageView == null) {
			return;
		}

		if (mMessage != null) {
			mMessageView.setText(mMessage);
		} else {
			mMessageView.setVisibility(View.GONE);
			mScrollView.removeView(mMessageView);

			if (mListView != null) {
				contentPanel.removeView(findViewById(R.id.scrollView));
				contentPanel.addView(mListView,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
				contentPanel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f));
			} else {
				contentPanel.setVisibility(View.GONE);
			}
		}
	}

	private boolean setupButtons() {
		if (!initialized) {
			return false;
		}
		int BIT_BUTTON_POSITIVE = 1;
		int BIT_BUTTON_NEGATIVE = 2;
		int BIT_BUTTON_NEUTRAL = 4;
		int whichButtons = 0;
		if (!mReversal) {
			mButtonPositive = (Button) findViewById(R.id.button2);
			mButtonNegative = (Button) findViewById(R.id.button1);
		} else {
			mButtonPositive = (Button) findViewById(R.id.button2);
			mButtonNegative = (Button) findViewById(R.id.button1);
		}
		mButtonPositive.setOnClickListener(mButtonHandler);

		if (TextUtils.isEmpty(mButtonPositiveText)) {
			mButtonPositive.setVisibility(View.GONE);
		} else {
			mButtonPositive.setText(mButtonPositiveText);
			mButtonPositive.setVisibility(View.VISIBLE);
			whichButtons = whichButtons | BIT_BUTTON_POSITIVE;
		}

		mButtonNegative.setOnClickListener(mButtonHandler);

		if (TextUtils.isEmpty(mButtonNegativeText)) {
			mButtonNegative.setVisibility(View.GONE);
		} else {
			mButtonNegative.setText(mButtonNegativeText);
			mButtonNegative.setVisibility(View.VISIBLE);

			whichButtons = whichButtons | BIT_BUTTON_NEGATIVE;
		}

		mButtonNeutral = (Button) findViewById(R.id.button3);
		mButtonNeutral.setOnClickListener(mButtonHandler);

		if (TextUtils.isEmpty(mButtonNeutralText)) {
			mButtonNeutral.setVisibility(View.GONE);
		} else {
			mButtonNeutral.setText(mButtonNeutralText);
			mButtonNeutral.setVisibility(View.VISIBLE);

			whichButtons = whichButtons | BIT_BUTTON_NEUTRAL;
		}

		if (AppConfig.CENTER_SINGLE_BUTTON) {
            /*
             * If we only have 1 button it should be centered on the layout and
             * expand to fill 50% of the available space.
             */
			if (whichButtons == BIT_BUTTON_POSITIVE) {
				centerButton(mButtonPositive);
			} else if (whichButtons == BIT_BUTTON_NEGATIVE) {
				centerButton(mButtonNegative);
			} else if (whichButtons == BIT_BUTTON_NEUTRAL) {
				centerButton(mButtonNeutral);
			}
		}

		applyButtons();

		return whichButtons != 0;
	}

	private void centerButton(Button button) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.weight = 0.5f;
		button.setLayoutParams(params);
		View leftSpacer = findViewById(R.id.leftSpacer);
		if (leftSpacer != null) {
			leftSpacer.setVisibility(View.VISIBLE);
		}
		View rightSpacer = findViewById(R.id.rightSpacer);
		if (rightSpacer != null) {
			rightSpacer.setVisibility(View.VISIBLE);
		}
	}

	public void setButton(int whichButton, CharSequence text, DialogInterface.OnClickListener listener, Message msg) {

		if (msg == null && listener != null) {
			msg = mHandler.obtainMessage(whichButton, listener);
		}

		switch (whichButton) {

			case DialogInterface.BUTTON_POSITIVE:
				mButtonPositiveText = text;
				mButtonPositiveMessage = msg;
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				mButtonNegativeText = text;
				mButtonNegativeMessage = msg;
				break;

			case DialogInterface.BUTTON_NEUTRAL:
				mButtonNeutralText = text;
				mButtonNeutralMessage = msg;
				break;

			default:
				throw new IllegalArgumentException("Button does not exist");
		}
	}


	private static final class ButtonHandler extends Handler {
		// Button clicks have Message.what as the BUTTON{1,2,3} constant
		private static final int MSG_DISMISS_DIALOG = 1;

		private WeakReference<DialogInterface> mDialog;

		public ButtonHandler(DialogInterface dialog) {
			mDialog = new WeakReference<>(dialog);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DialogInterface.BUTTON_POSITIVE:
				case DialogInterface.BUTTON_NEGATIVE:
				case DialogInterface.BUTTON_NEUTRAL:
					((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(), msg.what);
					break;
				case MSG_DISMISS_DIALOG:
					((DialogInterface) msg.obj).dismiss();
			}
		}
	}

	private boolean setupTitle(LinearLayout topPanel) {
		boolean hasTitle = true;

		if (mCustomTitleView != null) {
			// Add the custom title view directly to the topPanel layout
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

			topPanel.addView(mCustomTitleView, 0, lp);

			// Hide the title template
			View titleTemplate = findViewById(R.id.title_template);
			titleTemplate.setVisibility(View.GONE);
		} else {
			final boolean hasTextTitle = !TextUtils.isEmpty(mTitle);

			mIconView = (ImageView) findViewById(R.id.icon);
			if (hasTextTitle) {
                /* Display the title if a title is supplied, else hide it */
				mTitleView = (TextView) findViewById(R.id.alertTitle);

				mTitleView.setText(mTitle);

                /* Do this last so that if the user has supplied any
                 * icons we use them instead of the default ones. If the
                 * user has specified 0 then make it disappear.
                 */
				if (mIconId > 0) {
					mIconView.setImageResource(mIconId);
				} else if (mIcon != null) {
					mIconView.setImageDrawable(mIcon);
				} else if (mIconId == 0) {

                    /* Apply the padding from the icon to ensure the
                     * title is aligned correctly.
                     */
					mTitleView.setPadding(mIconView.getPaddingLeft(),
					                      mIconView.getPaddingTop(),
					                      mIconView.getPaddingRight(),
					                      mIconView.getPaddingBottom());
					mIconView.setVisibility(View.GONE);
				}
			} else {

				// Hide the title template
				View titleTemplate = findViewById(R.id.title_template);
				titleTemplate.setVisibility(View.GONE);
				mIconView.setVisibility(View.GONE);
				topPanel.setVisibility(View.GONE);
				hasTitle = false;
			}
		}
		return hasTitle;
	}
	public void applyButtons() {
		if (mButtonPositiveText != null) {
			setButton(DialogInterface.BUTTON_POSITIVE, mButtonPositiveText,
			                 mPositiveButtonListener, null);
		}
		if (mButtonNegativeText != null) {
			setButton(DialogInterface.BUTTON_NEGATIVE, mButtonNegativeText,
			                 mNegativeButtonListener, null);
		}
		if (mButtonNeutralText != null) {
			setButton(DialogInterface.BUTTON_NEUTRAL, mButtonNeutralText,
			                 mNeutralButtonListener, null);
		}
	}

}
