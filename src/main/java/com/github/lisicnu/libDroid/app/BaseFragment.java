package com.github.lisicnu.libDroid.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.lisicnu.log4android.LogManager;


/**
 * when extends from this class,
 * should call  {@link #setHandler(android.os.Handler)} method to setHandler.
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class BaseFragment extends Fragment {

    private Handler mHandler;

    public Handler getHandler() {
        return mHandler;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    private void logLifeStatus(String lifeStatus) {
        LogManager.d("lifeStatus", lifeStatus.concat("  ").concat(getClass().getSimpleName()));
    }

    @Override
    public void onAttach(Activity activity) {
        logLifeStatus("onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        logLifeStatus("onCreate: param is null:" + (savedInstanceState == null));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        logLifeStatus("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logLifeStatus("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        logLifeStatus("onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        logLifeStatus("onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        logLifeStatus("onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        logLifeStatus("onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        logLifeStatus("onStop");
        super.onStop();
    }

    @Override
    public void onTrimMemory(int level) {
        logLifeStatus("onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onDestroy() {
        logLifeStatus("onDestroy");
        recycle();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        logLifeStatus("onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        logLifeStatus("onDestroyView");
        super.onDestroyView();
    }

    protected void showToast(final String toastMsg) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showToast(toastMsg);
        } else {
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void showToast(int resId) {
        if (getHandler() != null && getActivity() != null) {
            String msg = getActivity().getString(resId);
            showToast(msg);
        }
    }

    /**
     * recycle all resources has been used.
     */
    protected void recycle() {
    }

}
