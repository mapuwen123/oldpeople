package com.aiminerva.oldpeople.globalsettings;

import android.os.Environment;

import com.aiminerva.oldpeople.MyApplication;

import java.io.File;
import java.util.HashMap;

/**
 * 爱家医生常量新app可以忽略
 */
public class GlobalConstants {

    public static final String HOST_URL = "http://182.92.228.143";// 未使用

    public static final String HOST_URL_SECURITY = "http://www.aiminerva.com/imi";// http
//	public static final String HOST_URL_SECURITY = "http://192.168.1.105:8080/imi";// http
//	public static final String HOST_URL_SECURITY = "http://182.50.3.216/imi";// http

    public static final String MD5_SALT = "FGF#^*%$77";

    public static final String HOST_AVATAR_URL = "http://www.aiminerva.com/";
    public static final String HOST = "http://59.110.48.213:8088";
    //public static final String HOST = "http://123.57.90.151:8088";
    public static final String LOGIN = HOST + "/aimihealth2/mobile/user/login";
    public static final String RECOMMEND_DOCTOR_LIST = HOST + "/aimihealth2/mobile/user/getRecommendDocList";
    public static final String RECOVER_LIST = HOST + "/aimihealth2/mobile/user/getCarouselPic";//轮播图
    public static final String FaultDiagnosisNum = HOST + "/aimihealth2/mobile/user/getFaultDiagnosisNum";
    public static final String getFlupTaskListForDoc = HOST + "/aimihealth2/mobile/flup/getFlupTaskListForDoc";//医生版随访预约
    //预约
    public static final String GOTO_ADD_BY_STAFF = HOST + "/aimihealth2/mobile/flup/goToAddByStaff";
    //取消预约
    public static final String GOTOCANCEL = HOST + "/aimihealth2/mobile/flup/goToCancel";

    public static final String getFlupDiagnoseList = HOST + "/aimihealth2/mobile/flup/getFlupDiagnoseList";
    public static final String GetFamilyMemberList = HOST + "/aimihealth2/mobile/user/getFamilyMemberList";//我的家庭成员列表
    public static final String GETFAMILYRELATIONSHIPLIST = HOST + "/aimihealth2/mobile/user/getFamilyRelationshipList";//亲友关系列表
    public static final String CREATE_FAMILY_MEMBER = HOST + "/aimihealth2/mobile/user/createFamilyMember";//新增家庭成员
    public static final String UPDATE_FAMILY_MEMBER = HOST + "/aimihealth2/mobile/user/updateFamilyMember";//编辑家庭成员
    public static final String DELETE_FAMILY_MEMBER = HOST + "/aimihealth2/mobile/user/deleteFamilyMember";//刪除家庭成员
    public static final String GetHealthtipsTypeList = HOST + "/aimihealth2/mobile/info/getHealthtipsTypeList";
    public static final String GetHealthTipsList = HOST + "/aimihealth2/mobile/info/getHealthTipsList";
    public static final String GetNoticeList = HOST + "/aimihealth2/mobile/info/getNoticeList";
    public static final String uploadHealthData = HOST + "/aimihealth2/mobile/flup/uploadHealthData";
    public static final String uploadECG = HOST + "/aimihealth2/mobile/ecg/uploadECG";
    public static final String getEcgInfo = HOST + "/aimihealth2/mobile/ecg/getEcgInfo";
    //历史测评-返回该月内的该项体检的记录
    //"sessionid=7a959e33-f898-4a50-ab20-252a4f39048e&page=1&pagesize=10&detect_time=2017-01-01&detect_type=5"
    public static final String getHistoryDetectRecordList= HOST+"/aimihealth2/mobile/flup/getHistoryDetectRecordList";
    //历史测评-返回该月内的体检过的日期
    //"sessionid=7a959e33-f898-4a50-ab20-252a4f39048e&page=1&pagesize=10&detect_time=2017-01-01&detect_type=5"
    public static final String getHistoryDetectDaysList=HOST+"/aimihealth2/mobile/flup/getHistoryDetectDaysList";

    public static final String getRecentlyBloodglucoseList = HOST + "/aimihealth2/mobile/flup/getRecentlyBloodglucoseList";

    public static final String getRecentlyBloodpress = HOST + "/aimihealth2/mobile/flup/getRecentlyBloodpress";
    public static final String getRecentlyBloodpressList = HOST + "/aimihealth2/mobile/flup/getRecentlyBloodpressList";

    public static final String getRecentlyOximeter = HOST + "/aimihealth2/mobile/flup/getRecentlyOximeter";
    public static final String getRecentlyOximeterList = HOST + "/aimihealth2/mobile/flup/getRecentlyOximeterList";
    public static final String getSignDiagDetail =HOST+"/aimihealth2/mobile/flup/getSignDiagDetail";
    public static final String getRecentlyTemperatureList=HOST+"/aimihealth2/mobile/flup/getRecentlyTemperatureList";
    public static final String getSignDates=HOST+"/aimihealth2/mobile/flup/getSignDates";

    public static final String getDetctDataForApp = HOST + "/aimihealth2/mobile/flup/getDetctDataForApp";
    //public static final String getFlupTaskListNotDetect = HOST + "/aimihealth2/mobile/flup/getFlupTaskListNotDetect";//医生版本体检首页

    public static final String getUserProfileForQA = HOST + "/aimihealth2/mobile/user/getUserProfileForQA";
    public static final String updateUserHeadPic = HOST + "/aimihealth2/mobile/info/updateUserHeadPic";

    public static final String GET_SIGNDIAGNOSE_LIST = HOST + "/aimihealth2/mobile/flup/getSignDiagnoseList";//体征诊断列表
    public static final String FILL_FLUPIAGNOSE_RESULT = HOST + "/aimihealth2/mobile/flup/createFlupDiagResult";//随访诊断填写诊断信息
    public static final String FILL_SIGNDIAG_RESULT = HOST + "/aimihealth2/mobile/flup/createSignDiagResult";//医生填写体征诊断信息
    public static final String listResidentForApp = HOST + "/aimihealth2/mobile/user/listResidentForApp";//居民档案
    public static final String getResidentProfileForApp = HOST + "/aimihealth2/mobile/user/getResidentProfileForApp";//居民档案详情
    public static final String getFlupDetectResult = HOST + "/aimihealth2/mobile/flup/getFlupDetectResult";//随访诊断--诊断详情
    public static final String GET_SIGN_DATES = HOST + "/aimihealth2/mobile/flup/getSignDates";//体征诊断--诊断详情
    //统计接口--医生角色返回血糖，血氧，血压，心电四个设备未诊断的异常个数，
    //居民角色返回传入的日期所在月份的血糖，血氧，血压，心电四个设备未处理的体检个数
    public static final String GET_SIGNDIAGNOSE_STATISTICS = HOST + "/aimihealth2/mobile/flup/statistics";

    //随访诊断个人版
    public static final String getFlupDiagnoseListForResident = HOST + "/aimihealth2/mobile/flup/getFlupDiagnoseListForResident";
    //随访预约个人版本
    public static final String getFlupTaskListForResident = HOST + "/aimihealth2/mobile/flup/getFlupTaskListForResident";

    public static final String getFlupRemindList = HOST + "/aimihealth2/mobile/flup/getFlupRemindList";
    //随访提醒

    public static final String getFlupRemindCalendarForResident = HOST + "/aimihealth2/mobile/flup/getFlupRemindCalendarForResident";
    //随访提醒--月历

    //咨询记录
    public static final String getMyHealthAskList = HOST + "/aimihealth2/mobile/info/getMyHealthAskList";

    //咨询记录列表
    public static final String myHealthAskRecordList = HOST + "/aimihealth2/mobile/user/myHealthAskRecordList";

    //本社区的全部医生列表
    public static final String getDocList = HOST + "/aimihealth2/mobile/user/getDocList";


    //健康提问查看详情页面&&咨询记录---问题详情接口
    public static final String getHealthAskView = HOST + "/aimihealth2/mobile/info/getHealthAskView";
    //查看更多追问追答
    public static final String getHealthReplyList = HOST + "/aimihealth2/mobile/info/getHealthReplyList";

    //为回答点赞
    public static final String likeHealthAnswer = HOST + "/aimihealth2/mobile/info/likeHealthAnswer";

    //医生解答/追答以及提问者追问
    public static final String replyHealthAnswer = HOST + "/aimihealth2/mobile/info/replyHealthAnswer";

    //问题库类型列表
    public static final String getHealthAskTypeList = HOST + "/aimihealth2/mobile/info/getHealthAskTypeList";

    //健康提问列表--eg 呼吸内科的提问列表等
    public static final String getHealthAskList = HOST + "/aimihealth2/mobile/info/getHealthAskList";

    //发起提问
    public static final String createHealthAsk = HOST + "/aimihealth2/mobile/info/createHealthAsk";
    //发起提问无图
    public static final String createHealthAskWithoutImage = HOST + "/aimihealth2/mobile/info/createHealthAskWithoutImage";


    //远程问诊--找专家 left|right 列表
    public static final String getMasterList = HOST + "/aimihealth2/mobile/user/getMasterList";

    //远程问诊订单系统

    //订单预创建（由居民发起）传入参数：masterId，专家id   返回 专家不在线status=1000 创建失败 1001
    public static final String prepareOrder=HOST+"/aimihealth2/mobile/order/prepare";
    public static final String cancelOrder = HOST +"/aimihealth2/mobile/order/cancel";
    //订单确认（由专家发起）：
    public static final String verify=HOST+"/aimihealth2/mobile/order/verify";

    public static final String begin=HOST+"/aimihealth2/mobile/order/begin";
    public static final String end=HOST+"/aimihealth2/mobile/order/end";

    public static final String FEEDBACK=HOST+"/aimihealth2/mobile/user/feedback";

    public static final String CHECK_VERSION=HOST+"/aimihealth2/mobile/info/getVersion";








//	public static final String HOST_AVATAR_URL = "http://192.168.1.105:8080/";
//	public static final        String HOST_AVATAR_URL = "http://182.50.3.216/imi";

    // public static final String HOST_AVATAR_URL = "http://182.50.3.216/";
//	public static final String HOST_AVATAR_URL = "http://192.168.3.19:8080/imi";

    /**
     * define PATH directory
     */
    public static final String SDCARD_DIR = Environment
            .getExternalStorageDirectory().toString();
    public static final String NOSDCARD_DIR = MyApplication.getContext()
            .getFilesDir().toString();
    public static final String DOCUMENT_DIR = Environment
            .getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? SDCARD_DIR
            : NOSDCARD_DIR;

    public static final String ROOT_DIR = DOCUMENT_DIR + "/rootDir/";
    public static final String FILE_DIR = ROOT_DIR + "file/";
    public static final String PICTURE_DIR = ROOT_DIR + "Picture/";
    public static final String DB_DIR = ROOT_DIR + "database/";
    public static final String HEALTH_DIR = ROOT_DIR + "health/";

    public static final File FILE_ROOT_DIR = MAKE_DIR(ROOT_DIR);
    public static final File FILE_FILE_DIR = MAKE_DIR(FILE_DIR);
    public static final File FILE_PICTURE_DIR = new File(PICTURE_DIR);
    public static final File FILE_DB_DIR = MAKE_DIR(DB_DIR);
    public static final File FILE_HEALTH_DIR = MAKE_DIR(HEALTH_DIR);

    //-----------evaluation start
    public static final String getEvaluateTypeList = HOST + "/aimihealth2/mobile/info/getHealthEvaluteTypeList";//评测类型列表
    public static final String getEvaluateList = HOST + "/aimihealth2/mobile/info/getEvaluateList";//测评题目列表
    public static final String getEvaluateQuestionList = HOST + "/aimihealth2/mobile/info/getEvaluateQuestionList";//测评题目相关的问题列表
    public static final String getEvaluateSave = HOST + "/aimihealth2/mobile/info/saveAppHealthEvaluate";//提交评测信息
    public static final String getMyEvaluateList = HOST + "/aimihealth2/mobile/info/myHealthEvaluateList";//我的评测记录
    public static final String getMyEvaluateInfo = HOST + "/aimihealth2/mobile/info/myHealthEvaluateInfo";//评测记录详情
    public static final String saveEvaluateResult = HOST + "/aimihealth2/mobile/info/saveAppEvaluateResult";//医生填写回复结果
    //-----------evaluation end

    //测评的9种类型，对应的9个Id  start
    public static final String EXTRA_EVALUATE_TYPE_ID = "evaluateTypeId";
    public static final String EXTRA_EVALUATE_TYPE_NAME = "evaluateTypeName";
    public static final String PERSONAL_EXTRA_EVALUATE_Id = "personalEvaluateId";
    public static final String HEALTH_EVALUATE_ID = "healthEvaluateId";
    //测评的9种类型，对应的9个Id  end

    public static final int DOCTOR_EVALUATE_TYPE_REQUEST_CODE = 101;
    public static final int DOCTOR_EVALUATE_TYPE_RESULT_CODE = 102;
    public static final int PERSONAL_ADD_FAMILY_MEMBER = 103;
    public static final int REQUEST_DIAGNOSIS_RESULT_CODE = 104;
    public static final int REQUEST_APPOINTMENT_RESULT_CODE = 105;
    public static final int REQUEST_LOGOUT = 106;
    public static final int REQUEST_CODE_REPLY = 107;
    public static final int REQUEST_CODE_SENDQUESTION_FINISH = 108;
    public static final int RESULT_CODE_SENDQUESTION_FINISH = 109;
    public static final int REQUEST_CODE_PERSONAL_EVALUATE = 110;
    public static final int REQUEST_HEADPIC_REFRESH = 111;


    //家庭成员列表
    public static final String EXTRA_FAMILY_MEMBER_OBJ = "familyMemberObj";

    // public static File
    public static File MAKE_DIR(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs() == true ? file : null;
        }
        // boolean c = ConfigManager.isSdcardAvailable();
        return file;
    }

    ;

    // =================get SDCard information===================
    public static boolean isSdcardAvailable() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static String GenarateAvatarURL(String url) {
        if (url.contains("www.aiminerva.com")) {
            return url;
        }
        return HOST_AVATAR_URL + url;
    }

    //居民列表上的用户id
    public static final String RESIDENT_USER_ID = "resident_user_id";
    public static final String RESIDENT_USER_NAME = "resident_user_name";
    public static final String LIMITE_DATE = "limit_date";
    public static final String IS_FLUPTASK = "is_flup_task";//是否是随访预约--开始测量

    public static final String USER_ID = "user_id";

    //预约
    public static final String TIME = "time";
    public static final String DOCTOR = "doctor_name";
    public static final String CONTENT = "appoint_content";
    public static final String NAME = "name";
    public static final String TASKSTATE = "-1";
    public static final String RESIDENT = "resident";
    public static final String DATE = "hasAppointMentDay";
    public static final String VISITTYPE = "visittype";
    public static final String FOLLOWBEAN = "followbean";
    public static final String AskListBean = "healthask_id";
    public static final String fluptask_id = "fluptask_id";
    //

    //登陆设置注销等
    public static final String LOGOUT = HOST + "/aimihealth2/mobile/user/logout";//注销
    public static final String CHANGE_PASSWORD = HOST + "/aimihealth2/mobile/user/changePassword";//修改密码


    //设备连接状态
    public static final int BLUETOOTH_OFF = 0;//蓝牙未开启
    public static final int BLUETOOTH_ON = 1;//蓝牙已开启
    public static final int START_CONNECT = 2;//开始连接
    public static final int CONNECTED = 3;//已连接
    public static final int DISCONNECT = 4;//连接已断开
    public static final int UPLOADSUCCESS = 5;//数据上传完毕
    public static final int UPLOADFAILED = 6;//数据上传失败
    public static final int UPLOADING = 7;//数据上传失败
    public static final int CONNECT_FAILED = 8;//连接失败

    public static HashMap<Integer, String> DevicesTipsMap = new HashMap<>();

    static {
        DevicesTipsMap.put(BLUETOOTH_OFF, "蓝牙未开启");
        DevicesTipsMap.put(BLUETOOTH_ON, "蓝牙已开启");
        DevicesTipsMap.put(START_CONNECT, "正在连接...");
        DevicesTipsMap.put(CONNECTED, "已连接,正在测量");
//        DevicesTipsMap.put(DISCONNECT, "设备已断开");
        DevicesTipsMap.put(UPLOADSUCCESS, "上传完毕");
        DevicesTipsMap.put(UPLOADFAILED, "上传失败");
        DevicesTipsMap.put(UPLOADING, "正在上传,请耐心等待");
        DevicesTipsMap.put(CONNECT_FAILED, "连接失败");
    }

    public static final int FAT=4;


    public static final int ALL=1;
    public static final int PART=2;
    public static final String LOGIN_INFO = "login_info";
    public static final String RESULT_ID = "RESULT_ID";
    public static final String TARGET_ID = "target_id";
    public static final String TARGET_NAME = "target_name";


    // 设备类型
    public final static int DEVICE_TYPE_BLOODPRESSURE = 1;// ：血压
    public final static int DEVICE_TYPE_BLOODSUGER = 2;// ：血糖
    public final static int DEVICE_TYPE_BLOODOXYGEN = 3;// ：血氧
    public final static int DEVICE_TYPE_BODYFAT = 4;// ：体质
    public final static int DEVICE_TYPE_EGC = 5;// ：心电
    public final static int DEVICE_TYPE_TEMP = 7;// 体温

    public final static String BEGIN_ACTION="com.aimihealth.begin.order";
    public final static String ORDER_REFUSE_ACTION="com.aimihealth.order.refuse";
    public final static String EXPERT_UNDER_LINE="com.aimihealth.order.underline";
    public final static String ORDER_TIMEOUT="com.aimihealth.order.timeout";
    public final static String LOGIN_ACTION="com.aimihealth.login";
    public final static String USER_BEGIN_ACTION="com.aimihealth.user.begin";//用户进入计时，专家需要进入会话页面
    public final static String RESIDENT_REFUSED="com.aimihealth.resident_refuse";

    public final static String ROLE_EXPERT="3";
    public final static String ROLE_DOCTOR="1";
    public final static String ROLE_RIDENT="2";

    public final static String FINISH_MAIN="com.aimihealth.finish";
    public final static String FRIEND_CACHE_KEY="friend_cache_key";
    public final static String HOME_AD_CACHE_KEY="home_ad_cache_key";
    public final static String RECOMMAND_DOCTOR_LIST_KEY="recommand_doctor_list_key";
    public final static String LittleSecretary="2";//小秘书


    public final static int MANUAL=3;//手动采集
    public final static int AUTO=1;//设备采集



}
