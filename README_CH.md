1.介绍

Queen是一个用户数据收集框架，使用该框架可以在不影响APP性能的情况下收集用户对APP的操作数据，
如：点击，滑动等，同时采集APP运行时环境如位置，电量等。根据以上信息，APP开发商可以轻松洞察
用户对APP的使用体验，及时发现崩溃，意想不到的用户操作以及监控APP安全。同时，为保护用户信息
安全，框架对传送数据进行加密，并执行用户毫无感知的传送。

2. 使用说明

Queen的使用接口已经完全封装，调用简单。强烈建议下载example执行并查看效果

2.1. 单例

Queen是一个单例，一次使用仅初始化一次Queen，因此获取Queen实例的方式是：

Queen.getInstance();

2.2. 启动

Queen需要初始化设置来启动一些服务，所以可以通过执行方法：

init("type your domain session", getApplication());

来启动，其中domain session是跟业务相关服务端域名，为的是可以筛选出有相同域名的cookie，方便
后端将通过session id将业务数据同用户数据进行匹配。

2.3. 服务端URL

由于Queen收集的数据需要传送自服务端，而不在手机端有任何永久性的保存，此做法是基于安全考虑，
因此Queen在使用前必须设置服务端URL，否则收集的数据最终将被抛弃。设置服务端URL的方式是执行：

setUrl("type your server's URL");

2.4. Activity状态数据获取

为了跟踪用户的APP使用，需要了解用户打开或者关闭了哪个页面，因此可以在Activity生命周期的onResume
和onDestroy使用activityDataCollect方法来收集打开和关闭信息。

2.5. 退出

在退出的时候，应用线程彻底被杀死，导致最后一部分数据无法上传，此处可在onDestroy处执行该方法对主线程
进行延时处理，给数据上传争取时间。但是由于不能影响用户正常退出APP，基于可传便传，不传抛弃的思想，此处
将时间限制在500s以内。

appExitSend(Context)；

2.6. 触摸事件收集

触摸事件方法可在触摸事件传输时进行处理，因此需要在dispatchTouchEvent方法中调用Queen的处理方法：

recognizeViewEvent(Event ev, View v, Context context)；

2.7. 观察者

为了方便开发人员观察调试数据，Queen设置了观察者，作为Queen内部状态，数据查看的接口，注册方式是：

registerObserver(IQueenWatcher)

取消方式是：

unregisterObserver(IQueenWatcher)

2.8. 其他

使用Queen的安全特性，需要设置RSA公钥，为的是对数据进行RSA和AES加密，设置方法：

setRSAPublicKey(String)；

开发人员采集了用户数据，也许更希望研究这些数据同业务数据有何联系，从中找到相关联系，做出反应。Queen通过收集cookie的
session id来为用户与业务分析提供机会。使用该特性，请确保业务服务器的Domain已经正确设置，如果没有，可通过setDomain(String)方法进行修改，并将以下方法放置在cookie保存
方法中：

setSessionId(List);

处于安全或者用户隐私考虑，某些UI或者界面不希望被收集，可通过addAvoidView(View)来添加不希望被收集的view。
