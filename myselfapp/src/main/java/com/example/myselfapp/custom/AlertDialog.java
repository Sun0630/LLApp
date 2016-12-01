package com.example.myselfapp.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.myselfapp.AppConfig;
import com.example.myselfapp.R;

import java.lang.ref.WeakReference;

import static android.view.View.GONE;

/**
 * Created by admin on 2016/11/16.
 */

public class AlertDialog extends Dialog {
    private Button mButtonNegative;
    private CharSequence mButtonNegativeText;
    private Button mButtonNeutral;
    private CharSequence mButtonNeutralText;
    private Button mButtonPositive;
    private CharSequence mButtonPositiveText;
    private AlertDialog.ButtonHandler mHandler;
    private Drawable mIcon;
    private int mIconId;
    private ImageView mIconView;
    private CharSequence mMessage;
    private TextView mMessageView;
    private OnClickListener mNegativeButtonListener;
    private OnClickListener mNeutralButtonListener;
    private OnClickListener mPositiveButtonListener;
    private ScrollView mScrollView;
    private CharSequence mTitle;
    public View mCustomTitleView;
    private TextView mTitleView;
    private View mView;
    private int mViewLayoutResId;
    private ListView mListView;
    private Message mButtonPositiveMessage;
    private Message mButtonNegativeMessage;
    private Message mButtonNeutralMessage;
    private boolean initialized;
    private boolean mReversal;
    android.view.View.OnClickListener mButtonHandler;

    public AlertDialog(Context context) {
        super(context);
        this.mReversal = AppConfig.DIALOG_BUTTON_REVERSAL;
        this.mButtonHandler = new android.view.View.OnClickListener() {
            public void onClick(View v) {
                Message m = null;
                if(v == AlertDialog.this.mButtonPositive && AlertDialog.this.mButtonPositiveMessage != null) {
                    m = Message.obtain(AlertDialog.this.mButtonPositiveMessage);
                } else if(v == AlertDialog.this.mButtonNegative && AlertDialog.this.mButtonNegativeMessage != null) {
                    m = Message.obtain(AlertDialog.this.mButtonNegativeMessage);
                } else if(v == AlertDialog.this.mButtonNeutral && AlertDialog.this.mButtonNeutralMessage != null) {
                    m = Message.obtain(AlertDialog.this.mButtonNeutralMessage);
                }

                if(m != null) {
                    m.sendToTarget();
                }

                AlertDialog.this.mHandler.obtainMessage(1, AlertDialog.this).sendToTarget();
            }
        };
    }

    public AlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mReversal = AppConfig.DIALOG_BUTTON_REVERSAL;
        this.mButtonHandler = new android.view.View.OnClickListener() {
            public void onClick(View v) {
                Message m = null;
                if(v == AlertDialog.this.mButtonPositive && AlertDialog.this.mButtonPositiveMessage != null) {
                    m = Message.obtain(AlertDialog.this.mButtonPositiveMessage);
                } else if(v == AlertDialog.this.mButtonNegative && AlertDialog.this.mButtonNegativeMessage != null) {
                    m = Message.obtain(AlertDialog.this.mButtonNegativeMessage);
                } else if(v == AlertDialog.this.mButtonNeutral && AlertDialog.this.mButtonNeutralMessage != null) {
                    m = Message.obtain(AlertDialog.this.mButtonNeutralMessage);
                }

                if(m != null) {
                    m.sendToTarget();
                }

                AlertDialog.this.mHandler.obtainMessage(1, AlertDialog.this).sendToTarget();
            }
        };
    }

    protected AlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mReversal = AppConfig.DIALOG_BUTTON_REVERSAL;
        this.mButtonHandler = new android.view.View.OnClickListener() {
            public void onClick(View v) {
                Message m = null;
                if(v == AlertDialog.this.mButtonPositive && AlertDialog.this.mButtonPositiveMessage != null) {
                    m = Message.obtain(AlertDialog.this.mButtonPositiveMessage);
                } else if(v == AlertDialog.this.mButtonNegative && AlertDialog.this.mButtonNegativeMessage != null) {
                    m = Message.obtain(AlertDialog.this.mButtonNegativeMessage);
                } else if(v == AlertDialog.this.mButtonNeutral && AlertDialog.this.mButtonNeutralMessage != null) {
                    m = Message.obtain(AlertDialog.this.mButtonNeutralMessage);
                }

                if(m != null) {
                    m.sendToTarget();
                }

                AlertDialog.this.mHandler.obtainMessage(1, AlertDialog.this).sendToTarget();
            }
        };
    }

    public void setReversal(boolean reversal) {
        if(this.mReversal != reversal) {
            this.mReversal = reversal;
            this.setupButtons();
        }

    }

    static boolean canTextInput(View v) {
        if(v.onCheckIsTextEditor()) {
            return true;
        } else if(!(v instanceof ViewGroup)) {
            return false;
        } else {
            ViewGroup vg = (ViewGroup)v;
            int i = vg.getChildCount();

            do {
                if(i <= 0) {
                    return false;
                }

                --i;
                v = vg.getChildAt(i);
            } while(!canTextInput(v));

            return true;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initialized = true;
        this.mHandler = new AlertDialog.ButtonHandler(this);
        this.requestWindowFeature(1);
        this.setContentView(R.layout.dialog_layout);
        LinearLayout contentPanel = (LinearLayout)this.findViewById(R.id.contentPanel);
        this.setupContent(contentPanel);
        boolean hasButtons = this.setupButtons();
        LinearLayout topPanel = (LinearLayout)this.findViewById(R.id.topPanel);
        boolean hasTitle = this.setupTitle(topPanel);
        View buttonPanel = this.findViewById(R.id.buttonPanel);
        if(!hasButtons) {
            buttonPanel.setVisibility(8);
            this.setCanceledOnTouchOutside(true);
        }

        FrameLayout customPanel = (FrameLayout)this.findViewById(R.id.customPanel);
        View customView;
        if(this.mView != null) {
            customView = this.mView;
        } else if(this.mViewLayoutResId != 0) {
            customView = this.getLayoutInflater().inflate(this.mViewLayoutResId, customPanel, false);
        } else {
            customView = null;
        }

        boolean hasCustomView = customView != null;
        if(!hasCustomView || !canTextInput(customView)) {
            this.getWindow().setFlags(131072, 131072);
        }

        if(hasCustomView) {
            FrameLayout divider = (FrameLayout)this.findViewById(R.id.custom);
            divider.addView(customView, new ViewGroup.LayoutParams(-1, -1));
            if(this.mListView != null) {
                ((android.widget.LinearLayout.LayoutParams)customPanel.getLayoutParams()).weight = 0.0F;
            }
        } else {
            this.findViewById(R.id.customPanel).setVisibility(8);
        }

        if(hasTitle) {
            View divider1 = null;
            if(this.mMessage != null || this.mView != null || this.mListView != null) {
                divider1 = this.findViewById(R.id.titleDivider);
            }

            if(divider1 != null) {
                divider1.setVisibility(0);
            }
        }

    }

    public void setTitle(int titleId) {
        this.mTitle = this.getContext().getText(titleId);
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
    }

    public void setMessage(CharSequence message) {
        this.mMessage = message;
    }

    public void setMessage(int messageId) {
        this.setMessage(this.getContext().getString(messageId));
    }

    public void setPositiveButton(CharSequence text, OnClickListener listener) {
        this.mButtonPositiveText = text;
        this.mPositiveButtonListener = listener;
        this.setupButtons();
    }

    public void setPositiveButton(int textId, OnClickListener listener) {
        this.setPositiveButton(this.getContext().getString(textId), listener);
    }

    public void setNegativeButton(CharSequence text, OnClickListener listener) {
        this.mButtonNegativeText = text;
        this.mNegativeButtonListener = listener;
        this.setupButtons();
    }

    public void setNegativeButton(int textId, OnClickListener listener) {
        this.setNegativeButton(this.getContext().getString(textId), listener);
    }

    public void setNeutralButton(CharSequence text, OnClickListener listener) {
        this.mButtonNeutralText = text;
        this.mNeutralButtonListener = listener;
        this.setupButtons();
    }

    public void setNeutralButton(int textId, OnClickListener listener) {
        this.setNeutralButton(this.getContext().getString(textId), listener);
    }

    public void setView(int layoutResId) {
        this.mView = null;
        this.mViewLayoutResId = layoutResId;
    }

    public void setView(View view) {
        this.mView = view;
        this.mViewLayoutResId = 0;
    }

    public void setIcon(int iconId) {
        this.mIconId = iconId;
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
    }

    private void setupContent(LinearLayout contentPanel) {
        this.mScrollView = (ScrollView)this.findViewById(R.id.scrollView);
        this.mScrollView.setFocusable(false);
        this.mMessageView = (TextView)this.findViewById(R.id.message);
        if(this.mMessageView != null) {
            if(this.mMessage != null) {
                this.mMessageView.setText(this.mMessage);
            } else {
                this.mMessageView.setVisibility(8);
                this.mScrollView.removeView(this.mMessageView);
                if(this.mListView != null) {
                    contentPanel.removeView(this.findViewById(R.id.scrollView));
                    contentPanel.addView(this.mListView, new android.widget.LinearLayout.LayoutParams(-1, -1));
                    contentPanel.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, 0, 1.0F));
                } else {
                    contentPanel.setVisibility(8);
                }
            }

        }
    }

    private boolean setupButtons() {
        if(!this.initialized) {
            return false;
        } else {
            boolean BIT_BUTTON_POSITIVE = true;
            boolean BIT_BUTTON_NEGATIVE = true;
            boolean BIT_BUTTON_NEUTRAL = true;
            boolean whichButtons = false;
            if(!this.mReversal) {
                this.mButtonPositive = (Button)this.findViewById(R.id.button2);
                this.mButtonNegative = (Button)this.findViewById(R.id.button1);
            } else {
                this.mButtonPositive = (Button)this.findViewById(R.id.button2);
                this.mButtonNegative = (Button)this.findViewById(R.id.button1);
            }

            this.mButtonPositive.setOnClickListener(this.mButtonHandler);
            if(TextUtils.isEmpty(this.mButtonPositiveText)) {
                this.mButtonPositive.setVisibility(8);
            } else {
                this.mButtonPositive.setText(this.mButtonPositiveText);
                this.mButtonPositive.setVisibility(0);
                whichButtons |= BIT_BUTTON_POSITIVE;
            }

            this.mButtonNegative.setOnClickListener(this.mButtonHandler);
            if(TextUtils.isEmpty(this.mButtonNegativeText)) {
                this.mButtonNegative.setVisibility(8);
            } else {
                this.mButtonNegative.setText(this.mButtonNegativeText);
                this.mButtonNegative.setVisibility(0);
                whichButtons |= BIT_BUTTON_NEGATIVE;
            }

            this.mButtonNeutral = (Button)this.findViewById(R.id.button3);
            this.mButtonNeutral.setOnClickListener(this.mButtonHandler);
            if(TextUtils.isEmpty(this.mButtonNeutralText)) {
                this.mButtonNeutral.setVisibility(8);
            } else {
                this.mButtonNeutral.setText(this.mButtonNeutralText);
                this.mButtonNeutral.setVisibility(0);
                whichButtons |= BIT_BUTTON_NEUTRAL;
            }

            if(AppConfig.CENTER_SINGLE_BUTTON) {
                if(whichButtons == BIT_BUTTON_POSITIVE) {
                    this.centerButton(this.mButtonPositive);
                } else if(whichButtons == BIT_BUTTON_NEGATIVE) {
                    this.centerButton(this.mButtonNegative);
                } else if(whichButtons == BIT_BUTTON_NEUTRAL) {
                    this.centerButton(this.mButtonNeutral);
                }
            }

            this.applyButtons();
            return whichButtons;
        }
    }

    private void centerButton(Button button) {
        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams)button.getLayoutParams();
        params.gravity = 1;
        params.weight = 0.5F;
        button.setLayoutParams(params);
        View leftSpacer = this.findViewById(R.id.leftSpacer);
        if(leftSpacer != null) {
            leftSpacer.setVisibility(0);
        }

        View rightSpacer = this.findViewById(R.id.rightSpacer);
        if(rightSpacer != null) {
            rightSpacer.setVisibility(0);
        }

    }

    public void setButton(int whichButton, CharSequence text, OnClickListener listener, Message msg) {
        if(msg == null && listener != null) {
            msg = this.mHandler.obtainMessage(whichButton, listener);
        }

        switch(whichButton) {
            case -3:
                this.mButtonNeutralText = text;
                this.mButtonNeutralMessage = msg;
                break;
            case -2:
                this.mButtonNegativeText = text;
                this.mButtonNegativeMessage = msg;
                break;
            case -1:
                this.mButtonPositiveText = text;
                this.mButtonPositiveMessage = msg;
                break;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }

    }

    private boolean setupTitle(LinearLayout topPanel) {
        boolean hasTitle = true;
        View titleTemplate;
        if(this.mCustomTitleView != null) {
            android.widget.LinearLayout.LayoutParams hasTextTitle = new android.widget.LinearLayout.LayoutParams(-1, -2);
            topPanel.addView(this.mCustomTitleView, 0, hasTextTitle);
            titleTemplate = this.findViewById(R.id.title_template);
            titleTemplate.setVisibility(8);
        } else {
            boolean hasTextTitle1 = !TextUtils.isEmpty(this.mTitle);
            this.mIconView = (ImageView)this.findViewById(R.id.icon);
            if(hasTextTitle1) {
                this.mTitleView = (TextView)this.findViewById(R.id.alertTitle);
                this.mTitleView.setText(this.mTitle);
                if(this.mIconId > 0) {
                    this.mIconView.setImageResource(this.mIconId);
                } else if(this.mIcon != null) {
                    this.mIconView.setImageDrawable(this.mIcon);
                } else if(this.mIconId == 0) {
                    this.mTitleView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
                    this.mIconView.setVisibility(8);
                }
            } else {
                titleTemplate = this.findViewById(R.id.title_template);
                titleTemplate.setVisibility(8);
                this.mIconView.setVisibility(8);
                topPanel.setVisibility(8);
                hasTitle = false;
            }
        }

        return hasTitle;
    }

    public void applyButtons() {
        if(this.mButtonPositiveText != null) {
            this.setButton(-1, this.mButtonPositiveText, this.mPositiveButtonListener, (Message)null);
        }

        if(this.mButtonNegativeText != null) {
            this.setButton(-2, this.mButtonNegativeText, this.mNegativeButtonListener, (Message)null);
        }

        if(this.mButtonNeutralText != null) {
            this.setButton(-3, this.mButtonNeutralText, this.mNeutralButtonListener, (Message)null);
        }

    }

    private static final class ButtonHandler extends Handler {
        private static final int MSG_DISMISS_DIALOG = 1;
        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialog) {
            this.mDialog = new WeakReference(dialog);
        }

        public void handleMessage(Message msg) {
            switch(msg.what) {
                case -3:
                case -2:
                case -1:
                    ((OnClickListener)msg.obj).onClick((DialogInterface)this.mDialog.get(), msg.what);
                case 0:
                default:
                    break;
                case 1:
                    ((DialogInterface)msg.obj).dismiss();
            }

        }
    }
}

