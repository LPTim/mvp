# mvp<br>
[#简书地址](https://www.jianshu.com/p/df4eee78085c)<br>
__使用方法__<br>
1、新建实体类<br>
```
/**
 * File descripition:
 *
 * @author lp
 * @date 2018/9/19
 */
public class MainBean {
    /**
     * id : 11
     * act_logo : ""
     * play_time : 2018-06-10
     * name : 中国生物质能源产业联盟会员代表大会
     * province : 北京市
     * city : 西城区
     */
    private int id;
    private String act_logo;
    private String play_time;
    private String name;
    private String province;
    private String city;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getAct_logo() {
        return act_logo;
    }
    public void setAct_logo(String act_logo) {
        this.act_logo = act_logo;
    }

    public String getPlay_time() {
        return play_time;
    }
    public void setPlay_time(String play_time) {
        this.play_time = play_time;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
}
```
2、新建对应的接口回调view
```
/**
 * File descripition:
 *
 * @author lp
 * @date 2018/6/19
 */
public interface MainView extends BaseView {
    void onMainSuccess(BaseModel<List<MainBean>> o);
}
```
3、新建对应的请求Presenter
```
/**
 * File descripition:
 *
 * @author lp
 * @date 2018/6/19
 */

public class MainPresenter extends BasePresenter<MainView> {
    public MainPresenter(MainView baseView) {
        super(baseView);
    }

    public void commentAdd() {
        addDisposable(apiServer.getMain("year"), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                baseView.onMainSuccess((BaseModel<List<MainBean>>) o);
            }

            @Override
            public void onError(String msg) {
                if (baseView != null) {
                    baseView.showError(msg);
                }
            }
        });
    }
}
```
4、在activity实现Presenter，比如mainActivity
```
public class MainActivity extends BaseActivity<MainPresenter> implements MainView, View.OnClickListener {
    private TextView tv_msg;
    private Button btn;
    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }
    @Override
    protected void initData() {
        tv_msg = findViewById(R.id.tv_msg);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }
    @Override
    public void onMainSuccess(BaseModel<List<MainBean>> o) {
        //数据返回
        tv_msg.setText(o.getData().toString());
    }
    @Override
    public void onClick(View v) {
        //数据请求
        mPresenter.commentAdd();
    }
}
```
