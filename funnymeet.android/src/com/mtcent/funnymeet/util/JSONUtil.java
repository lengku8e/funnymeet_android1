package com.mtcent.funnymeet.util;

import android.content.Context;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Json工具类
 */
public class JSONUtil {

    public static final String TimeInfo = "infot";
    /*
    保存JSON字符串到本地
 */
    public static void saveJstr(String url, JSONObject json) {
        try {
            String name = StrUtil.md5(url);
            FileOutputStream outputStream = SOApplication.getAppContext()
                    .openFileOutput(name, Context.MODE_PRIVATE);
            outputStream.write(json.toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /*
        访问指定链接获取JSON返回对象
     */
    public static JSONObject getJstrFormNet(String url,
                                            List<NameValuePair> params, String paramjstr, long time) {
        JSONObject json = null;
        InputStream inputStream = null;

        HttpPost httpPost = new HttpPost(url);
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);

        System.out.println("运行到这里了");
        try {
            //如果请求参数不为空
            if (paramjstr != null) {
                httpPost.addHeader("Content-Type", "application/json");
                JSONObject jsonObj = createNetParamJstr(paramjstr, time);
                System.out.print("json数据：" + jsonObj.toString());
                httpPost.setEntity(new StringEntity(jsonObj.toString(),
                        HTTP.UTF_8));
            } else {
                System.out.println("运行到:paramjstr为空");
                httpPost.setHeader("Content-Type",
                        "application/x-www-form-urlencoded;charset=utf-8");
                params.add(new BasicNameValuePair("infot", String.valueOf(time)));
                HttpEntity entity = new UrlEncodedFormEntity(
                        createNetParam(params), HTTP.UTF_8);
                httpPost.setEntity(entity);
            }
            // 下面使用Http客户端发送请求，并获取响应内容
            // 发送请求并获得响应对象
            System.out.println("请求内容:" + httpPost.getRequestLine());
            HttpResponse mHttpResponse = httpClient.execute(httpPost);

            // 获得响应的消息实体
            HttpEntity mHttpEntity = mHttpResponse.getEntity();
            // 获取一个输入流
            inputStream = mHttpEntity.getContent();

            // 如果返回状态为200，获得返回的结果
            int status = mHttpResponse.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                // 获取
                String jsonString = StrUtil.readString(inputStream);
                json = new JSONObject(jsonString);

                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return json;
    }

    /*
 创建JSON对象，并将请求参数保存其中
*/
    private static JSONObject createNetParamJstr(String paramjstr, long time) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("infot", time);
            jsonObj.put("guid", getGUID());
            String pkName = SOApplication.getAppContext().getPackageName();
            String versionCode = SOApplication.getAppContext()
                    .getPackageManager().getPackageInfo(pkName, 0).versionCode
                    + "";

            jsonObj.put("app_version", versionCode);// 应用版本
            jsonObj.put("device_id", StrUtil.getDivId());

            if (paramjstr != null) {
                jsonObj.put("jstr", new JSONObject(paramjstr));
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return jsonObj;
    }

    /*
        创建请求参数列表
     */
    private static List<NameValuePair> createNetParam(List<NameValuePair> p) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.addAll(p);
        try {
            String pkName = SOApplication.getAppContext().getPackageName();
            String versionCode;
            params.add(new BasicNameValuePair("guid", getGUID()));
            params.add(new BasicNameValuePair("device_id", StrUtil.getDivId()));
            // params.add(new BasicNameValuePair("sysver", "android"
            // + android.os.Build.VERSION.RELEASE));
            versionCode = SOApplication.getAppContext().getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode
                    + "";
            params.add(new BasicNameValuePair("app_version", versionCode));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /*
        生成GUID
     */
    public static String getGUID() {
        String guid = UserMangerHelper.getDefaultUserGuid();

        if (guid == null) {
            guid = "0";
        }
        return guid;
    }

    public static JSONObject getJstr(String url) {
        JSONObject json = null;
        String name = StrUtil.md5(url);
        try {

            File f = new File(name);
            if (!f.exists()) {
                return null;
            }
            FileInputStream inputStream = SOApplication.getAppContext()
                    .openFileInput(name);
            String jstr = StrUtil.readString(inputStream);
            if (jstr != null && jstr.length() > 0) {
                json = new JSONObject(jstr);
                if (json.has(TimeInfo) == false || json.optLong(TimeInfo) == 0) {
                    SOApplication.getAppContext().deleteFile(name);
                    json = null;
                }
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            SOApplication.getAppContext().deleteFile(name);
        }

        return json;
    }
}
