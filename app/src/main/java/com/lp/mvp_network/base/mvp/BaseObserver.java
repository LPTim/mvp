package com.lp.mvp_network.base.mvp;

import com.google.gson.JsonParseException;
import com.lp.mvp_network.base.ApiException;
import com.lp.mvp_network.base.BaseContent;
import com.lp.mvp_network.utils.NetWorkUtils;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

/**
 * File descripition:   数据处理基类
 *
 * @author lp
 * @date 2018/6/19
 */

public abstract class BaseObserver<T> extends DisposableObserver<T> {

    public static final int CODE = BaseContent.basecode;

    protected BaseView view;
    /**
     * 网络连接失败  无网
     */
    public static final int NETWORK_ERROR = 100000;
    /**
     * 解析数据失败
     */
    public static final int PARSE_ERROR = 1008;
    /**
     * 网络问题
     */
    public static final int BAD_NETWORK = 1007;
    /**
     * 连接错误
     */
    public static final int CONNECT_ERROR = 1006;
    /**
     * 连接超时
     */
    public static final int CONNECT_TIMEOUT = 1005;

    /**
     * 未登录  与服务器约定返回的值   这里未登录只是一个案例
     */
    public static final int CONNECT_NOT_LOGIN = 1001;
    /**
     * 其他code  提示
     */
    public static final int OTHER_MESSAGE = 20000;


    public BaseObserver(BaseView view) {
        this.view = view;
    }

    @Override
    protected void onStart() {
        if (view != null) {
            view.showLoading();
        }
    }

    @Override
    public void onNext(T o) {
        try {
            if (view != null) {
                view.hideLoading();
            }
            BaseModel model = (BaseModel) o;
            if (model.getErrcode() == CODE) {
                onSuccess(o);
                /*服务器返回的指定成功 code  设置是否回调  解开注释回调*//*
                if (view != null) {
                    view.onErrorCode(model);
                }*/
            } else {
                if (view != null) {
                    view.onErrorCode(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e.toString());
        }
    }

    @Override
    public void onError(Throwable e) {
        if (view != null) {
            view.hideLoading();
        }
        if (e instanceof HttpException) {
            //   HTTP错误
            onException(BAD_NETWORK, "");
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {
            //   连接错误
            onException(CONNECT_ERROR, "");
        } else if (e instanceof InterruptedIOException) {
            //  连接超时
            onException(CONNECT_TIMEOUT, "");
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            //  解析错误
            onException(PARSE_ERROR, "");
            e.printStackTrace();


            /**
             * 此处很重要
             * 为何这样写：因为开发中有这样的需求   当服务器返回假如0是正常 1是不正常  当返回0时：我们gson 或 fastJson解析数据
             * 返回1时：我们不想解析（可能返回值出现以前是对象 但是现在数据为空变成了数组等等，于是在不改后台代码的情况下  我们前端需要处理）
             * 但是用了插件之后没有很有效的方法控制解析 所以处理方式为  当服务器返回不等于0时候  其他状态都抛出异常 然后提示
             * 代码上一级在 MyGsonResponseBodyConverter 中处理  前往查看逻辑
             */
        } else if (e instanceof ApiException) {
            ApiException exception = (ApiException) e;
            int code = exception.getErrorCode();
            switch (code) {
                //未登录（此处只是案例 供理解）
                case CONNECT_NOT_LOGIN:
                    view.onErrorCode(new BaseModel(exception.getMessage(), code));
                    onException(CONNECT_NOT_LOGIN, "");
                    break;
                //其他不等于0 的所有状态
                default:
                    onException(OTHER_MESSAGE, exception.getMessage());
                    view.onErrorCode(new BaseModel(exception.getMessage(), code));
                    break;
            }
        } else {
            if (e != null) {
                onError(e.toString());
            } else {
                onError("未知错误");
            }
        }

    }

    /**
     * 中间拦截一步  判断是否有网络  这步判断相对准确  此步去除也可以
     *
     * @param unknownError
     * @param message
     */
    private void onException(int unknownError, String message) {
        BaseModel model = new BaseModel(message, unknownError);
        if (!NetWorkUtils.isAvailableByPing()) {
            model.setErrcode(NETWORK_ERROR);
            model.setErrmsg("网络不可用，请检查网络连接！");
        }
        onExceptions(model.getErrcode(), model.getErrmsg());
        if (view != null) {
            view.onErrorCode(model);
        }
    }

    private void onExceptions(int unknownError, String message) {
        switch (unknownError) {
            case CONNECT_ERROR:
                onError("连接错误");
                break;
            case CONNECT_TIMEOUT:
                onError("连接超时");
                break;
            case BAD_NETWORK:
                onError("网络超时");
                break;
            case PARSE_ERROR:
                onError("数据解析失败");
                break;
            //未登录
            case CONNECT_NOT_LOGIN:
//                onError("未登录");
                break;
            //正常执行  提示信息
            case OTHER_MESSAGE:
                onError(message);
                break;
            //网络不可用
            case NETWORK_ERROR:
                onError("网络不可用，请检查网络连接！");
                break;
            default:
                break;
        }
    }

    //消失写到这 有一定的延迟  对dialog显示有影响
    @Override
    public void onComplete() {
       /* if (view != null) {
            view.hideLoading();
        }*/
    }

    public abstract void onSuccess(T o);

    public abstract void onError(String msg);
}
