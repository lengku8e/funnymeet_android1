package com.mtcent.funnymeet.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.mtcent.funnymeet.SOApplication;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class StrUtil {

	static final String LocalFileNameHead = "local:";// 数据来自本地文件
	static final String HttpFileNameHead = "http";// 数据来自网路
	static final String MemoryJStrHead = "jstr";// 数据来自内存JStr
	static final String region_Anhui = "安庆/蚌埠/亳州/长丰/巢湖/池州/滁州/枞阳/当涂/砀山/定远/东至/繁昌/肥东/肥西/凤台/凤阳/阜南/阜阳/固镇/广德/含山/合肥/和县/怀宁/怀远/淮北/淮南/黄山/黄山区/黄山市/霍邱/霍山/绩溪/界首/金寨/泾县/旌德/九华山/来安/郎溪/利辛/临泉/灵璧/六安/庐江/马鞍山/蒙城/明光/南陵/宁国/祁门/潜山/青阳/全椒/石台/寿县/舒城/泗县/宿松/宿州/濉溪/太和/太湖/天长/桐城/铜陵/屯溪/望江/涡阳/无为/芜湖/芜湖县/五河/歙县/萧县/休宁/宣城/黟县/颍上/岳西/";
	static final String region_AoMen = "澳门/";
	static final String regiion_BeiJing = "北京/昌平/大兴/房山/怀柔/门头沟/密云/平谷/顺义/通州/延庆/海淀/朝阳/丰台/石景山/";
	static final String region_ChongQiong = "巴南/北碚/璧山/长寿/城口/大足/垫江/丰都/奉节/涪陵/合川/江津/开县/梁平/南川/彭水/綦江/黔江/荣昌/石柱/铜梁/潼南/万盛/万州/巫山/巫溪/武隆/秀山/永川/酉阳/渝北/云阳/忠县/重庆/";
	static final String region_DiaoYuDao = "钓鱼岛/";
	static final String region_FuJian = "安溪/长乐/长泰/长汀/崇武/大田/德化/东山/福安/福鼎/福清/福州/古田/光泽/华安/建宁/建瓯/建阳/将乐/连城/连江/龙海/龙岩/罗源/闽侯/闽清/明溪/南安/南靖/南平/宁德/宁化/平和/平潭/屏南/莆田/浦城/清流/泉州/三明/沙县/上杭/邵武/寿宁/顺昌/松溪/泰宁/同安/武平/武夷山/霞浦/厦门/仙游/秀屿港/永安/永春/永定/永泰/尤溪/云霄/漳平/漳浦/漳州/诏安/柘荣/政和/周宁/惠安/石狮/晋江//";
	static final String region_GanSu = "麦积/白银/成县/皋兰/宕昌/迭部/崇信/东乡/定西/敦煌/甘谷/瓜州/合作/高台/古浪/徽县/广河/合水/和政/康县/华池/华亭/环县/礼县/会宁/两当/临潭/金昌/金塔/陇西/泾川/景泰/靖远/静宁/酒泉/碌曲/康乐/玛曲/兰州/岷县/临洮/临夏/临泽/灵台/民乐/民勤/秦安/清水/宁县/平凉/庆阳/山丹/天水/肃北/肃南/文县/武都/通渭/武山/西和/渭源/武威/西峰/夏河/张家川/永昌/漳县/永登/永靖/榆中/玉门镇/舟曲/卓尼/张掖/镇原/正宁/庄浪/嘉峪关/天祝/阿克塞/庆城/积石山/";
	static final String region_GuangXi = "巴马/百色/北海/北流/宾阳/博白/苍梧/岑溪/崇左/大新/德保/东兰/东兴/都安/防城/防城港/凤山/扶绥/富川/恭城/灌阳/贵港/桂林/桂平/合浦/河池/贺州/横县/环江/金秀/靖西/来宾/乐业/荔浦/临桂/灵川/灵山/凌云/柳城/柳江/柳州/龙胜/龙州/隆安/隆林/陆川/鹿寨/罗城/马山/蒙山/那坡/南丹/南宁/宁明/平果/平乐/平南/凭祥/浦北/钦州/全州/容县/融安/融水/三江/上林/上思/藤县/天等/天峨/田东/田林/田阳/涠洲岛/梧州/武鸣/武宣/西林/象州/忻城/兴安/阳朔/宜州/邕宁/永福/玉林/昭平/钟山/资源/兴业/大化/合山/ ";
	static final String region_GuangDong = "博罗/潮阳/潮州/澄海/从化/大埔/德庆/电白/遂溪/东莞/斗门/恩平/番禺/丰顺/封开/佛冈/佛山/高州/广宁/广州/海丰/和平/河源/花都/化州/怀集/惠东/惠来/惠州/江门/蕉岭/揭西/揭阳/开平/乐昌/雷州/连南/连平/连山/连州/廉江/龙川/龙门/陆丰/罗定/茂名/梅州/南澳/南海/南雄/平远/普宁/清远/饶平/仁化/乳源/三水/汕头/汕尾/韶关/深圳/始兴/顺德/四会/台山/翁源/吴川/五华/新丰/新会/新兴/信宜/兴宁/徐闻/阳春/阳江/阳山/英德/郁南/云浮/增城/湛江/肇庆/中山/珠海/紫金/鹤山/高要/梅县/陆河/东源/阳西/阳东/清新/潮安/揭东/云安/惠阳/";
	static final String region_GuiZhou = "安龙/安顺/白云/毕节/岑巩/册亨/长顺/赤水/大方/从江/丹寨/道真/德江/都匀/独山/凤冈/福泉/关岭/赫章/贵定/贵阳/花溪/江口/黄平/金沙/惠水/开阳/剑河/锦屏/六盘水/凯里/雷山/黎平/荔波/湄潭/六枝/龙里/纳雍/罗甸/麻江/盘县/普安/黔西/平坝/平塘/仁怀/普定/清镇/晴隆/施秉/榕江/石阡/三都/三穗/思南/松桃/绥阳/桐梓/铜仁/万山/威宁/台江/瓮安/务川/天柱/息烽/习水/望谟/乌当/沿河/兴仁/兴义/修文/印江/余庆/玉屏/镇远/正安/织金/贞丰/镇宁/紫云/遵义/遵义县/水城/";
	static final String region_HaiNan = "白沙/保亭/昌江/澄迈/儋州/定安/东方/海口/乐东/临高/陵水/南沙/琼海/琼中/三亚/屯昌/万宁/文昌/五指山/西沙/";
	static final String region_HeBei = "安国/安平/安新/北戴河/霸州/柏乡/保定/泊头/沧州/昌黎/大名/成安/承德/任县/承德县/赤城/崇礼/磁县/大厂/大城/定州/东光/肥乡/丰南/丰宁/丰润/馆陶/峰峰/抚宁/阜城/阜平/高碑店/高阳/高邑/藁城/沽源/固安/故城/广平/广宗/海兴/邯郸/河间/衡水/怀安/怀来/黄骅/鸡泽/临西/冀州/晋洲/井陉/景县/巨鹿/康保/宽城/涞源/廊坊/乐亭/蠡县/临城/临漳/灵寿/隆化/隆尧/卢龙/栾城/滦南/滦平/滦县/满城/孟村/内邱/南宫/南和/南皮/宁晋/邱县/平泉/平山/平乡/迁安/迁西/秦皇岛/青龙/青县/清河/曲阳/曲周/饶阳/任丘/容城/三河/沙河/尚义/涉县/深泽/深州/石家庄/顺平/肃宁/威县/唐海/唐山/唐县/万全/望都/围场/蔚县/魏县/文安/无极/吴桥/武安/武强/武邑/献县/香河/辛集/新河/新乐/兴隆/行唐/邢台/雄县/徐水/宣化/盐山/阳原/易县/永年/永清/玉田/元氏/赞皇/枣强/张北/张家口/赵县/正定/涿鹿/涿州/遵化/涞水/鹿泉/清苑/定兴/博野/沧县/";
	static final String region_HeiLongJiang = "阿城/安达/巴彦/拜泉/宝清/北安/宾县/勃利/大庆/大兴安岭/东宁/抚远/方正/富锦/富裕/甘南/哈尔滨/海林/海伦/鹤岗/黑河/呼兰/呼玛/呼中/虎林/桦川/桦南/鸡东/鸡西/集贤/佳木斯/嘉荫/克东/克山/兰西/林甸/林口/龙江/萝北/密山/明水/漠河/牡丹江/木兰/穆棱/讷河/嫩江/宁安/七台河/齐齐哈尔/青冈/饶河/庆安/尚志/双城/双鸭山/绥滨/绥芬河/绥化/绥棱/孙吴/塔河/泰来/汤原/铁力/通河/同江/望奎/乌伊岭/五常/五大连池/五营/新林/逊克/延寿/伊春/依安/依兰/肇东/肇源/肇州/友谊/";
	static final String region_HeNan = "宝丰/安阳/长葛/博爱/长垣/郸城/夏邑/登封/邓州/范县/方城/扶沟/巩义/光山/封丘/固始/淮阳/郏县/淮滨/潢川/鹤壁/开封/滑县/兰考/辉县/获嘉/济源/临颍/灵宝/焦作/卢氏/鲁山/浚县/栾川/罗山/洛宁/洛阳/漯河/孟津/孟州/泌阳/林州/内乡/鹿邑/南乐/南阳/南召/民权/平舆/濮阳/宁陵/杞县/内黄/平顶山/清丰/确山/汝南/汝阳/汝州/三门峡/淇县/商水/上蔡/沁阳/社旗/渑池/商城/商丘/嵩县/遂平/台前/太康/沈丘/唐河/通许/桐柏/睢县/汤阴/尉氏/温县/舞钢/舞阳/西华/西平/西峡/息县/淅川/卫辉/襄城/项城/新安/新蔡/新密/新县/新野/新郑/信阳/许昌/武陟/鄢陵/偃师/叶县/伊川/新乡/宜阳/修武/荥阳/延津/禹州/永城/虞城/镇平/正阳/郑州/中牟/原阳/周口/柘城/驻马店/陕县/";
	static final String region_HuBei = "安陆/巴东/保康/蔡甸/长阳/赤壁/崇阳/大悟/大冶/丹江口/当阳/鄂州/恩施/房县/公安/谷城/广水/汉川/鹤峰/红安/洪湖/黄陂/黄冈/嘉鱼/监利/建始/江夏/黄梅/黄石/京山/荆门/荆州/来凤/老河口/利川/麻城/罗田/南漳/潜江/蕲春/三峡/神农架/十堰/石首/松滋/随州/天门/通城/通山/五峰/武汉/仙桃/咸丰/咸宁/襄阳/孝感/新洲/武穴/兴山/浠水/宣恩/宜昌/宜城/宜都/应城/阳新/英山/远安/云梦/郧西/郧县/枣阳/枝江/钟祥/竹山/竹溪/秭归/江陵/沙洋/孝昌/团风/";
	static final String region_HuNan = "安化/安仁/安乡/保靖/茶陵/长沙/常宁/常德/辰溪/郴州/城步/慈利/道县/东安/洞口/凤凰/古丈/汉寿/衡东/桂东/衡山/桂阳/花垣/华容/怀化/赫山区/吉首/衡南/衡阳/衡阳县/会同/嘉禾/江华/江永/冷水江/澧县/醴陵/涟源/临澧/临湘/靖州/浏阳/龙山/隆回/蓝山/娄底/泸溪/耒阳/冷水滩/麻阳/马坡岭/临武/汨罗/南县/南岳/宁乡/平江/宁远/祁东/祁阳/桑植/韶山/邵东/邵阳/汝城/石门/双峰/邵阳县/桃江/桃源/双牌/绥宁/通道/湘潭/湘乡/湘阴/新化/新晃/新邵/武冈/溆浦/新宁/新田/益阳/炎陵/永顺/攸县/宜章/沅江/沅陵/岳阳/永兴/张家界/永州/芷江/株洲/资兴/望城/洪江/中方/";
	static final String region_JiangSu = "宝应/滨海/常熟/常州/大丰/丹阳/东海/东台/丰县/阜宁/赣榆/高淳/高邮/灌南/灌云/海安/海门/洪泽/淮安/淮阴县/建湖/江都/江宁/江浦/江阴/姜堰/金湖/金坛/靖江/句容/昆山/溧水/溧阳/连云港/涟水/六合/南京/南通/沛县/邳州/启东/如东/如皋/射阳/沭阳/泗洪/泗阳/苏州/宿迁/睢宁/太仓/泰兴/泰州/无锡/吴江/响水/新沂/兴化/盱眙/徐州/盐城/扬中/扬州/仪征/宜兴/张家港/镇江/铜山/吴中/";
	static final String region_JiangXi = "安福/安义/安远/崇仁/崇义/大余/德安/德兴/定南/东乡/都昌/分宜/丰城/奉新/抚州/赣州/高安/广昌/广丰/贵溪/吉安/横峰/湖口/会昌/吉水/莲花/金溪/进贤/井冈山/景德镇/靖安/九江/乐安/乐平/黎川/龙南/庐山/南昌/南昌县/南城/南丰/南康/萍乡/宁都/宁冈/彭泽/鄱阳/铅山/上高/全南/瑞昌/瑞金/上饶/上饶县/上犹/石城/铜鼓/万载/遂川/泰和/万安/万年/武宁/新余/婺源/修水/峡江/新干/新建/信丰/兴国/星子/宜春/宜丰/寻乌/宜黄/弋阳/鹰潭/永丰/永新/永修/于都/余干/余江/玉山/樟树/资溪/浮梁/芦溪/上栗/赣县/ ";
	static final String region_JiLin = "安图/白城/白山/长白/长春/长岭/大安/德惠/东丰/东岗/敦化/扶余/公主岭/和龙/桦甸/珲春/辉南/吉林/集安/蛟河/靖宇/九台/梨树/辽源/临江/柳河/龙井/梅河口/农安/磐石/前郭/乾安/舒兰/双辽/双阳/四平/松原/洮南/通化/通化县/通榆/图们/汪清/延吉/伊通/永吉/榆树/镇赉/东辽/抚松/";
	static final String region_LiaoNing = "鞍山/朝阳/北票/本溪/本溪县/昌图/长海/东港/大连/大石桥/大洼/丹东/灯塔/法库/凤城/抚顺/阜新/盖州/海城/黑山/葫芦岛/桓仁/建昌/建平县/金州/锦州/喀左/开原/康平/宽甸/辽阳/辽阳县/辽中/凌海/凌源/旅顺/盘山/盘锦/普兰店/清原/沈阳/绥中/台安/铁岭/瓦房店/西丰/新民/兴城/岫岩/义县/营口/章党/彰武/庄河/新宾/";
	static final String region_NeiMengGu = "阿巴嘎旗/阿尔山/阿拉善右旗/阿拉善左旗/阿鲁科尔沁旗/阿荣旗/敖汉旗/八里罕/巴林右旗/巴林左旗/巴雅尔吐胡硕/巴彦诺尔贡/白云鄂博/包头/宝过图/博克图/察哈尔右翼后旗/察哈尔右翼前旗/察哈尔右翼中旗/陈巴尔虎旗/赤峰/达尔罕茂明安联合旗/达拉特旗/大佘太/磴口/东乌珠穆沁旗/多伦/额尔古纳/额济纳旗/鄂尔多斯/鄂伦春旗/鄂托克旗/鄂托克前旗/鄂温克旗/二连浩特/丰镇/岗子/高力板/根河/固阳/拐子湖/海拉尔/海力素/杭锦后旗/杭锦旗/浩尔吐/和林格尔/河南/呼和浩特/呼和浩特市郊区/胡尔勒/化德/吉兰太/集宁/喀喇沁旗/开鲁/科尔沁右翼中旗/科尔沁左翼后旗/科尔沁左翼中旗/克什克腾旗/库伦旗/凉城/林西/临河/满都拉/满洲里/莫力达瓦旗/那仁宝力格/奈曼旗/宁城/清水河/青龙山/商都/舍伯吐/四子王旗/苏尼特右旗/苏尼特左旗/索伦/太仆寺旗/通辽/头道湖/突泉/图里河/土默特右旗/土默特左旗/托克托/翁牛特旗/乌海/乌拉盖/乌拉特后旗/乌拉特前旗/乌拉特中旗/乌兰浩特/乌审旗/乌审召/五原/武川/西乌珠穆沁旗/希拉穆仁/锡林高勒/锡林浩特/镶黄旗/小二沟/新巴尔虎右旗/新巴尔虎左旗/兴和/牙克石/伊金霍洛旗/伊克乌素/扎赉特旗/扎兰屯/扎鲁特旗/正兰旗/正镶白旗/中泉子/朱日和/准格尔旗/卓资/霍林郭勒/";
	static final String region_NingXia = "彭阳/固原/海原/贺兰/惠农/泾源/灵武/隆德/平罗/青铜峡/石嘴山/陶乐/同心/吴忠/西吉/盐池/银川/永宁/中宁/中卫/";
	static final String region_QingHai = "班玛/达日/大柴旦/大通/都兰/甘德/果洛/格尔木/黄南/贵德/贵南/海北/海东/海南/海西/海晏/久治/互助/化隆/湟源/湟中/尖扎/玛多/乐都/冷湖/囊谦/茫崖/门源/民和/曲麻莱/祁连/天峻/同德/乌兰/西宁/兴海/循化/玉树/杂多/治多/泽库/刚察/河南/共和/玛沁/称多/德令哈/";
	static final String region_Shan3Xi = "安康/白河/宝鸡/安塞/彬县/白水/长安/城固/淳化/大荔/长武/丹凤/澄城/定边/凤县/凤翔/佛坪/扶风/富平/高陵/府谷/汉阴/汉中/富县/甘泉/户县/华县/华阴/韩城/合阳/泾阳/横山/岚皋/蓝田/黄陵/黄龙/礼泉/临潼/佳县/麟游/留坝/陇县/靖边/略阳/洛南/眉县/勉县/南郑/宁强/宁陕/洛川/平利/米脂/岐山/千阳/乾县/蒲城/三原/山阳/商南/清涧/石泉/商洛/神木/太白/绥德/潼关/渭南/武功/铜川/西安/西乡/咸阳/吴堡/吴旗/兴平/旬阳/洋县/耀县/旬邑/延安/永寿/延长/延川/宜川/宜君/镇安/镇巴/镇坪/榆林/周至/紫阳/柞水/志丹/子长/子洲/";
	static final String region_ShanDong = "安丘/博山/苍山/曹县/昌乐/昌邑/长岛/长清/成山头/成武/滨州/茌平/博兴/单县/定陶/东阿/东明/东平/肥城/费县/德州/东营/福山/高密/高唐/冠县/海阳/菏泽/高青/桓台/河口/广饶/即墨/济南/济宁/济阳/嘉祥/胶南/胶州/金乡/莒南/莒县/巨野/鄄城/垦利/莱芜/莱西/莱阳/莱州/崂山/惠民/梁山/聊城/临清/临朐/临沂/临淄/龙口/临沭/蒙阴/乐陵/利津/牟平/临邑/陵县/宁阳/蓬莱/平度/平邑/平阴/栖霞/齐河/青岛/青州/宁津/曲阜/日照/荣成/平原/乳山/庆云/石岛/寿光/商河/泗水/泰安/滕州/威海/潍坊/台儿庄/文登/汶上/五莲/郯城/夏津/微山/无棣/新泰/武城/烟台/兖州/阳谷/沂南/沂水/沂源/薛城/鱼台/禹城/阳信/峄城/郓城/招远/枣庄/周村/诸城/沾化/章丘/淄博/淄川/邹城/邹平/莘县/";
	static final String region_ShangHai = "宝山/崇明/奉贤/嘉定/金山/闵行/南汇/浦东/青浦/上海/松江/徐家汇/";
	static final String region_Shan1Xi = "安泽/保德/长治/长子/大宁/大同/大同县/代县/定襄/繁峙/方山/汾西/汾阳/浮山/高平/古县/广灵/和顺/河津/河曲/洪洞/侯马/壶关/怀仁/浑源/霍州/吉县/稷山/绛县/交城/交口/介休/晋城/晋中/静乐/岢岚/岚县/柳林/离石/黎城/临汾/临县/临猗/灵邱/灵石/陵川/娄烦/潞城/吕梁/平陆/宁武/平遥/偏关/蒲县/平定/平鲁/平顺/芮城/祁县/沁水/沁县/沁源/清徐/曲沃/山阴/神池/石楼/寿阳/朔州/太谷/太原/尖草坪区/太原古交区/小店区/天镇/屯留/万荣/夏县/文水/闻喜/五台山/五寨/武乡/昔阳/隰县/乡宁/襄汾/襄垣/孝义/忻州/新绛/兴县/永济/阳城/阳高/阳曲/阳泉/翼城/应县/永和/右玉/盂县/榆次/榆社/垣曲/原平/运城/中阳/左权/左云/泽州/五台县/五台县豆村/";
	static final String region_SiChuang = "阿坝/安县/安岳/巴塘/巴中/白玉/宝兴/北川/苍溪/长宁/成都/崇州/达州/大邑/大竹/丹巴/丹棱/布拖/道孚/纳溪/稻城/得荣/德昌/德格/德阳/都江堰/峨边/峨眉/东兴/峨眉山/富顺/甘洛/甘孜/高县/珙县/古蔺/广安/广汉/广元/汉源/合江/黑水/红原/洪雅/会东/会理/夹江/犍为/简阳/剑阁/江安/江油/金川/金堂/金阳/井研/九寨沟/筠连/开江/康定/阆中/乐山/乐至/雷波/理塘/理县/凉山/邻水/龙泉驿/隆昌/芦山/泸定/泸县/泸州/炉霍/马边/马尔康/茂县/眉山/美姑/米易/绵阳/绵竹/冕宁/名山/木里/沐川/内江/南部/南充/南江/南溪/宁南/攀枝花/彭山/彭州/蓬安/蓬溪/郫县/平昌/平武/屏山/蒲江/普格/青川/青神/邛崃/渠县/壤塘/仁和/仁寿/荣经/荣县/若尔盖/三台/色达/射洪/什邡/石棉/石渠/双流/松潘/遂宁/天全/通江/万源/旺苍/威远/温江/汶川/武胜/西充/喜德/乡城/小金/新都/新津/新龙/兴文/西昌/叙永/宣汉/雅安/雅江/盐边/盐亭/盐源/仪陇/宜宾/宜宾县/营山/岳池/越西/昭觉/中江/资阳/资中/梓潼/自贡/罗江/华蓥/九龙/";
	static final String region_TaiWan = "高雄/台北/台中/新竹/宜兰/嘉义/台南/台东/花莲/桃园/屏东/苗栗/彰化/南投/云林/";
	static final String region_TianJin = "北辰/宝坻/大港/东丽/汉沽/津南/蓟县/静海/宁河/天津/塘沽/西青/武清/";
	static final String region_XinJiang = "阿合奇/阿克苏/阿克陶/阿拉尔/阿拉山口/阿勒泰/阿图什/阿瓦提/巴楚/巴里坤/巴仑台/巴音布鲁克/拜城/博乐/布尔津/蔡家湖/策勒/察布查尔/昌吉/塔中/达坂城/额敏/福海/阜康/富蕴/伽师/巩留/哈巴河/哈密/和布克赛尔/和静/和硕/和田/呼图壁/霍城/霍尔果斯/吉木乃/吉木萨尔/精河/喀什/柯坪/克拉玛依/库车/库尔勒/轮台/洛浦/玛纳斯/麦盖提/米泉/民丰/莫索湾/墨玉/木垒/尼勒克/炮台/皮山/奇台/且末/青河/若羌/沙湾/沙雅/莎车/鄯善/石河子/塔城/塔什库尔干/特克斯/天池/铁干里克/吐鲁番/托克逊/托里/尉犁/温泉/温宿/乌鲁木齐/乌鲁木齐牧试站/乌恰/乌什/乌苏/小渠子/新和/新源/焉耆/叶城/伊宁/伊宁县/伊吾/英吉沙/于田/裕民/岳普湖/泽普/昭苏/疏附/疏勒/博湖/奎屯/";
	static final String region_XiangGang = "香港/新界/";
	static final String region_XiZang = "阿里/安多/班戈/波密/察隅/昌都/错那/当雄/丁青/定日/改则/加查/嘉黎/江孜/拉萨/拉孜/浪卡子/林芝/隆子/洛隆/芒康/米林/那曲/南木林/尼木/聂拉木/帕里/日喀则/山南/申扎/狮泉河/索县/左贡/类乌齐/八宿/贡嘎/琼结/比如/林周/曲水/堆龙德庆/达孜/墨竹工卡/江达/贡觉/察雅/边坝/札囊/桑日/曲松/措美/洛扎/萨迦/昂仁/谢通门/白朗/仁布/康马/定结/仲巴/吉隆/萨嘎/岗巴/聂荣/巴青/尼玛/普兰/札达/日土/革吉/措勤/工布江达/墨脱/朗县/";
	static final String region_YunNan = "安宁/保山/宾川/沧源/昌宁/呈贡/澄江/楚雄/大理/大姚/德宏/德钦/东川/峨山/洱源/凤庆/福贡/富民/富宁/富源/个旧/耿马/广南/鹤庆/红河/华宁/华坪/会泽/建水/剑川/江城/江川/金平/晋宁/景东/景谷/开远/昆明/兰坪/澜沧/丽江/梁河/临沧/景洪/六库/龙陵/陇川/泸西/鲁甸/陆良/禄丰/禄劝/绿春/罗平/麻栗坡/马关/马龙/勐海/猛腊/蒙自/孟连/弥渡/弥勒/墨江/牟定/南华/南涧/宁蒗/怒江/屏边/普洱/巧家/丘北/曲靖/瑞丽/师宗/施甸/石林/石屏/双柏/双江/嵩明/绥江/太华山/腾冲/通海/威信/维西/文山/武定/西畴/西盟/祥云/新平/宣威/寻甸/盐津/砚山/漾鼻/姚安/宜良/彝良/易门/盈江/永德/永平/永仁/永善/永胜/玉溪/元江/元谋/元阳/云龙/云县/沾益/昭通/镇康/镇雄/镇沅/中甸/大关/水富/宁洱/巍山/贡山/";
	static final String region_ZheJiang = "安吉/长兴/常山/淳安/慈溪/岱山/德清/东阳/奉化/富阳/海宁/海盐/杭州/洪家/湖州/嘉善/嘉兴/建德/江山/金华/缙云/开化/兰溪/乐清/丽水/临安/龙泉/龙游/宁波/宁海/平湖/平阳/浦江/普陀/青田/庆元/衢州/瑞安/三门/上虞/绍兴/嵊泗/嵊州/遂昌/台州/泰顺/天台/桐庐/桐乡/温岭/温州/文成/武义/仙居/象山/萧山/新昌/义乌/永嘉/永康/余姚/玉环/云和/舟山/诸暨/临海/洞头/鄞州/苍南/磐安/松阳/景宁/";

	static TreeMap<String, String> regionOfProvince;
	public final static String[] getProvince = { "安徽", "澳门", "北京", "重庆", "钓鱼岛",
			"福建", "甘肃", "广西", "广东", "贵州", "海南", "河北", "黑龙江", "河南", "江苏", "江西",
			"吉林", "辽宁", "内蒙古", "宁夏", "青海", "陕西", "山东", "上海", "山西", "四川", "台湾",
			"天津", "新疆", "香港", "西藏", "浙江" };

	public final static TreeMap<String, String> getRegionOfProvince() {
		regionOfProvince = new TreeMap<String, String>(new ProvinceComparator());
		regionOfProvince.put("安徽", region_Anhui);
		regionOfProvince.put("澳门", region_AoMen);
		regionOfProvince.put("北京", regiion_BeiJing);
		regionOfProvince.put("重庆", region_ChongQiong);
		regionOfProvince.put("钓鱼岛", region_DiaoYuDao);
		regionOfProvince.put("福建", region_FuJian);
		regionOfProvince.put("甘肃", region_GanSu);
		regionOfProvince.put("广西", region_GuangXi);
		regionOfProvince.put("广东", region_GuangDong);
		regionOfProvince.put("贵州", region_GuiZhou);
		regionOfProvince.put("海南", region_HaiNan);
		regionOfProvince.put("河北", region_HeBei);
		regionOfProvince.put("黑龙江", region_HeiLongJiang);
		regionOfProvince.put("河南", region_HeNan);
		regionOfProvince.put("江苏", region_JiangSu);
		regionOfProvince.put("江西", region_JiangXi);
		regionOfProvince.put("吉林", region_JiLin);
		regionOfProvince.put("辽宁", region_LiaoNing);
		regionOfProvince.put("内蒙古", region_NeiMengGu);
		regionOfProvince.put("宁夏", region_NingXia);
		regionOfProvince.put("青海", region_QingHai);
		regionOfProvince.put("陕西", region_Shan3Xi);
		regionOfProvince.put("山东", region_ShanDong);
		regionOfProvince.put("上海", region_ShangHai);
		regionOfProvince.put("山西", region_Shan1Xi);
		regionOfProvince.put("四川", region_SiChuang);
		regionOfProvince.put("台湾", region_TaiWan);
		regionOfProvince.put("天津", region_TianJin);
		regionOfProvince.put("新疆", region_XinJiang);
		regionOfProvince.put("香港", region_XiangGang);
		regionOfProvince.put("西藏", region_XiZang);
		regionOfProvince.put("浙江", region_ZheJiang);

		return regionOfProvince;

	}

	public static String getFirstLetterFromPinyin(String str) {
		String first_letter;
		String[] pinyin = null;

		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

		try

		{
			pinyin = PinyinHelper.toHanyuPinyinStringArray(str.charAt(0),
					format);
		}

		catch (BadHanyuPinyinOutputFormatCombination e)

		{
			e.printStackTrace();
		}

		// 如果c不是汉字，toHanyuPinyinStringArray会返回null

		if (pinyin == null) {
			first_letter = String.valueOf(str.charAt(0)).toUpperCase();
		} else {
			first_letter = String.valueOf(pinyin[0].charAt(0)).toUpperCase();
		}

		return first_letter;
	}

	public static boolean isLocalFile(String url) {
		if (url != null) {
			return url.startsWith(LocalFileNameHead);
		}
		return false;
	}

	public static boolean isHttpFile(String url) {
		if (url != null) {
			return url.startsWith(HttpFileNameHead);
		}
		return false;
	}

	public static String getLocalFileName(String url) {
		if (isLocalFile(url)) {
			return url.substring(LocalFileNameHead.length());
		}
		return null;
	}

	public static boolean isJStr(String url) {
		if (url != null) {
			return url.startsWith(MemoryJStrHead);
		}
		return false;
	}

	public static long oneDay = 1000 * 60 * 60 * 24;
	protected static MessageDigest messagedigest = null;
	static {
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsaex) {
			// System.err.println(MD5Util.class .getName() +
			// "初始化失败，MessageDigest不支持MD5Util。" );
			nsaex.printStackTrace();
		}
	}

	static Tencent mTencent = null;
	static QQShare mQQShare = null;
	static QzoneShare mQzShare = null;
	static final String QQapiid = "1101463891";

	public static void openTencent() {
		openQQShare();
		openQzShare();

	}

	static void openQQShare() {
		if (mQQShare == null) {
			QQAuth mQQAuth = QQAuth.createInstance(QQapiid,
					SOApplication.getAppContext());
			QQToken tk = mQQAuth.getQQToken();
			mQQShare = new QQShare(SOApplication.getAppContext(), tk);
		}
	}

	static void openQzShare() {
		if (mQzShare == null) {
			QQAuth mQQAuth = QQAuth.createInstance(QQapiid,
					SOApplication.getAppContext());
			QQToken tk = mQQAuth.getQQToken();
			mQzShare = new QzoneShare(SOApplication.getAppContext(), tk);
		}
	}

	public static void colosTencent() {
		mQQShare = null;
		mQzShare = null;

	}

	// 分享到腾讯QQ
	public static void shareToQQ(Activity activity, String title,
			String summar, String targetUrl, String imageUrl) {
		openQQShare();
		if (mQQShare != null) {

			Bundle params = new Bundle();

			params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
					QQShare.SHARE_TO_QQ_TYPE_DEFAULT);

			if (title == null)
				title = "";

			params.putString(QQShare.SHARE_TO_QQ_TITLE, title);// 必填

			if (summar != null) {
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summar);// 可选
			}
			if (targetUrl != null) {
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);// 必填
			}

			if (imageUrl != null) {
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);// 可选
			}

			mQQShare.shareToQQ(activity, params, new IUiListener() {
				@Override
				public void onCancel() {
				}

				@Override
				public void onComplete(Object response) {
				}

				@Override
				public void onError(UiError e) {
				}
			});
		}
	}

	// 分享到腾讯QQ空间
	public static void shareToQzone(Activity activity, String title,
			String summar, String targetUrl, String imageUrl) {
		openQzShare();
		if (mQzShare != null) {
			Bundle params = new Bundle();
			params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
					QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
			if (title == null)
				title = "";

			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);// 必填

			if (summar == null)
				summar = "";

			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summar);// 选填

			if (targetUrl != null)
				params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);// 必填

			if (imageUrl != null) {
				ArrayList<String> imageUrls = new ArrayList<String>();
				imageUrls.add(imageUrl);
				params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
						imageUrls);// 可选
			}

			mQzShare.shareToQzone(activity, params, new IUiListener() {
				@Override
				public void onCancel() {
				}

				@Override
				public void onComplete(Object response) {
				}

				@Override
				public void onError(UiError e) {
				}
			});
		}
	}

	private static IWXAPI mIWXAPI = null;
	static String WXAPIID = "wx38548added26b21b";

	// package="kzd.freecondom.web"

	// static String WXAPIID = "wxd930ea5d5a258f4f";
	// <--!package="net.sourceforge.simcpux" -->
	static void openIWXAPI() {
		if (mIWXAPI == null) {
			mIWXAPI = WXAPIFactory.createWXAPI(SOApplication.getAppContext(),
					WXAPIID);
		}
		mIWXAPI.registerApp(WXAPIID);
	}

	public static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	// 分享到腾讯微信
	public static boolean shareToTencentWeiXin(String title, String summar,
			String targetUrl, String imageUrl) {
		openIWXAPI();
		if (mIWXAPI != null) {
			if (mIWXAPI.isWXAppInstalled() == false) {
				return false;
			}
			WXMediaMessage localWXMediaMessage = new WXMediaMessage();

			localWXMediaMessage.title = title;
			localWXMediaMessage.description = summar;

			Bitmap bitmap = BitmapUtil.getThumbImage(imageUrl, 480);
			if (bitmap != null) {
				localWXMediaMessage.setThumbImage(bitmap);
			}

			com.tencent.mm.sdk.openapi.WXWebpageObject localWXWebpageObject = new com.tencent.mm.sdk.openapi.WXWebpageObject();
			localWXWebpageObject.webpageUrl = targetUrl;
			localWXMediaMessage.mediaObject = localWXWebpageObject;

			SendMessageToWX.Req localReq = new SendMessageToWX.Req();
			localReq.transaction = buildTransaction("text");
			localReq.message = localWXMediaMessage;
			localReq.scene = SendMessageToWX.Req.WXSceneSession;// 对话框
			mIWXAPI.sendReq(localReq);
		}
		return true;
	}

	// 分享到腾讯微信的朋友圈
	public static boolean shareToTencentPengYou(String title, String summar,
			String targetUrl, String imageUrl) {
		openIWXAPI();

		if (mIWXAPI != null) {
			if (mIWXAPI.isWXAppInstalled() == false) {
				return false;
			}
			WXMediaMessage localWXMediaMessage = new WXMediaMessage();

			localWXMediaMessage.title = title;
			localWXMediaMessage.description = summar;

			Bitmap bitmap = BitmapUtil.getThumbImage(imageUrl, 480);
			if (bitmap != null) {
				localWXMediaMessage.setThumbImage(bitmap);
			}

			com.tencent.mm.sdk.openapi.WXWebpageObject localWXWebpageObject = new com.tencent.mm.sdk.openapi.WXWebpageObject();
			localWXWebpageObject.webpageUrl = targetUrl;
			localWXMediaMessage.mediaObject = localWXWebpageObject;

			SendMessageToWX.Req localReq = new SendMessageToWX.Req();
			localReq.transaction = buildTransaction("text");
			localReq.message = localWXMediaMessage;
			localReq.scene = SendMessageToWX.Req.WXSceneTimeline;// 朋友圈
			mIWXAPI.sendReq(localReq);
		}
		return true;
	}

	// 分享到腾讯微博
	public static void shareToTencentWeiBo(String title, String summar,
			String targetUrl, String imageUrl) {

	}

	// 分享到新浪微博
	public static boolean shareToSinaWeiBo(final Activity activity,
			String title, String summar, String targetUrl, String imageUrl) {
		boolean send = false;
		// 创建微博 SDK 接口实例
		IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity,
				"1907702359");
		// 注册
		boolean reg = mWeiboShareAPI.registerApp();

		// 获取微博客户端相关信息，如是否安装、支持 SDK 的版本
		boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
		int supportApiLevel = mWeiboShareAPI.getWeiboAppSupportAPI();

		// 如果未安装微博客户端，设置下载微博对应的回调
		if (!isInstalledWeibo) {
			mWeiboShareAPI
					.registerWeiboDownloadListener(new IWeiboDownloadListener() {
						@Override
						public void onCancel() {
							Toast.makeText(activity, "取消下载", Toast.LENGTH_SHORT)
									.show();
						}
					});
		} else if (reg) {
			// 1. 初始化微博的分享消息
			WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

			TextObject textObject = new TextObject();
			textObject.text = title + " " + summar;
			weiboMessage.textObject = textObject;

			if (imageUrl != null) {
				try {
					Bitmap bitmap = BitmapUtil.getThumbImage(imageUrl, 0);
					if (bitmap != null) {
						ImageObject imageObject = new ImageObject();
						imageObject.setImageObject(bitmap);
						weiboMessage.imageObject = imageObject;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
			if (targetUrl != null) {

				WebpageObject mediaObject = new WebpageObject();
				mediaObject.identify = Utility.generateGUID();
				mediaObject.title = title;
				mediaObject.description = summar;

				Bitmap bitmap = BitmapUtil.getThumbImage(imageUrl, 480);
				if (bitmap != null) {
					mediaObject.setThumbImage(bitmap);
				}

				mediaObject.actionUrl = targetUrl;
				mediaObject.defaultText = title + " " + summar;

				weiboMessage.mediaObject = mediaObject;
			}

			// 2. 初始化从第三方到微博的消息请求
			SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
			// 用transaction唯一标识一个请求
			request.transaction = String.valueOf(System.currentTimeMillis());
			request.multiMessage = weiboMessage;

			// 3. 发送请求消息到微博，唤起微博分享界面
			send = mWeiboShareAPI.sendRequest(request);

		}
		return send;
	}

	public static String getVersionName(Context context) {

		try {
			String pkName = context.getPackageName();
			String versionName = context.getPackageManager().getPackageInfo(
					pkName, 0).versionName;
			// int versionCode = this.getPackageManager().getPackageInfo(pkName,
			// 0).versionCode;
			return versionName;
		} catch (Exception e) {

		}
		return "";

	}

	// 网络是否可用
	public static boolean networkDetect(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return false;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		} else {
			// int netType = networkinfo.getType();
			// ConnectivityManager.TYPE_MOBILE, TYPE_WIFI, TYPE_WIMAX,
			// TYPE_ETHERNET, TYPE_BLUETOOTH,
		}
		return true;
	}

	public static String getMapUrl(String addr) {
		return "http://map.baidu.com/m?word=" + addr + "&fr=ala_open";
	}

	// 调用浏览器打开网页
	public static void openUrl(Context mContext, String url) {
		Uri uri = Uri.parse(url);
		mContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}

	public static void openTel(Context mContext, String tel) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + tel));
		mContext.startActivity(intent);
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// 获取屏幕宽度Px
	public static int getScreenWidePx() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager mWm = (WindowManager) SOApplication.getAppContext()
				.getSystemService(Context.WINDOW_SERVICE);
		mWm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	// 获取屏幕宽度Dp
	public static int getScreenWideDp() {
		return px2dip(null, getScreenWidePx());
	}

	// 获取屏幕宽度Px
	public static int getScreenHightPx() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager mWm = (WindowManager) SOApplication.getAppContext()
				.getSystemService(Context.WINDOW_SERVICE);
		mWm.getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	// 获取屏幕宽度Dp
	public static int getScreenHightDp() {
		return px2dip(null, getScreenHightPx());
	}

	// 获取屏幕宽度Dp
	public static int getModuleM(int layoutWide, int moduleWide) {
		int ModuleM = 0;
		int num = layoutWide / moduleWide;
		if (num > 0) {
			ModuleM = ((layoutWide - (num * moduleWide)) / num) / 2;
		}
		return ModuleM;
	}

	// 判断时间是否是今天
	public static boolean isToday(long time) {
		long now = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String today = formatter.format(new Date(now));
		String timeday = formatter.format(new Date(time));
		if (timeday.equals(today)) {
			return true;
		}
		return false;
	}

	// 一个将字符串随意转换的函数，不同字符串返回不通的数字
	public static int hashcode(String s) {
		int hash = 0;
		byte val[] = s.getBytes();
		int len = val.length;
		for (int i = 0; i < len; i++) {
			hash <<= 1;
			if (hash < 0) {
				hash |= 1;
			}
			hash ^= val[i];
		}
		return hash;
	}

	// 判断字符串的MD5加密，密码判断
	public static boolean checkByMD5(String passWord, String MD5) {

		if (messagedigest != null && MD5 != null
				&& md5(passWord).toLowerCase().equals(MD5.toLowerCase())) {
			return true;
		}
		return false;
	}

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	// 获取字符串的MD5
	public static String md5(String s) {

		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			String mm = new String(messageDigest);
			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	// 将
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	// 半角转化为全角的方法
	public static String ToSBC(String input) {
		// 半角转全角：
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {

			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127 && c[i] > 32)
				c[i] = (char) (c[i] + 65248);

		}
		return new String(c);
	}

	// 将中文字符串中的英文前后加空格，防止自动换行导致中英文不对齐
	public static String trimForLine(String input) {
		char[] c = input.toCharArray();
		char[] newc = new char[c.length * 2];
		int newI = 0;
		for (int i = 0; i < c.length; i++, newI++) {
			newc[newI] = c[i];
			if (c[i] < 127 && c[i] > 32) {
				if (i > 0 && c[i - 1] > 127) {
					newc[newI] = ' ';
					newI++;
					newc[newI] = c[i];
				}
				if (i < c.length - 1 && c[i + 1] > 127) {
					newI++;
					newc[newI] = ' ';
				}
			}

		}
		return new String(newc, 0, newI + 1);
	}

	public static String timerConversion(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm ");

		// 是否是今日

		if (isToday(time)) {
			formatter = new SimpleDateFormat("今天 HH:mm ");
		}
		return formatter.format(new Date(time));
	}

	public static String timerConversionString(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
		return formatter.format(new Date(time));
	}

	public static String getStringFormAssets(String name) {
		String str = null;
		try {
			InputStream in = SOApplication.getAppContext().getAssets()
					.open(name);
			str = readString(in);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}

	public static String readString(InputStream in) {
		String str = null;

		try {
			byte[] data = new byte[1024];
			int length = 0;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			while ((length = in.read(data)) != -1) {
				bout.write(data, 0, length);
			}
			str = new String(bout.toByteArray(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;

	}

	public static ArrayList<String> getListForJsonArray(String jstr) {
		ArrayList<String> list = new ArrayList<String>();

		try {
			JSONArray json = new JSONArray(jstr);
			int leng = json.length();
			for (int i = 0; i < leng; i++) {
				list.add(new String(json.getString(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

	public static String[] getStringListForJsonArray(String jstr) {
		String[] charlist = {};
		try {
			JSONArray json = new JSONArray(jstr);
			int leng = json.length();
			if (leng > 0) {
				charlist = new String[leng];
				for (int i = 0; i < leng; i++) {
					charlist[i] = new String(json.getString(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return charlist;
	}

	public static int dip2px(float dpValue) {
		return dip2px(null, dpValue);
	}
	
	public static int dip2px(Context context, float dpValue) {
		if (context == null) {
			context = SOApplication.getAppContext();
		}
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		if (context == null) {
			context = SOApplication.getAppContext();
		}
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static boolean isTopActivity(Context context,
			String activityClassName) {
		List<RunningTaskInfo> tasksInfo = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (activityClassName.equals(tasksInfo.get(0).topActivity
					.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTopActivity(Context context) {
		String activityName = context.getClass().getName();
		return isTopActivity(context, activityName);
	}

	public static boolean isTopApplication(Context context, String packageName) {
		List<RunningTaskInfo> tasksInfo = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTopApplication(Context context) {
		String packageName = context.getPackageName();
		return isTopApplication(context, packageName);
	}

	/**
	 * 判断是否黑屏
	 * 
	 * @param c
	 * @return
	 */
	public final static boolean isScreenLocked(Context c) {

		KeyguardManager mKeyguardManager = (KeyguardManager) c
				.getSystemService(c.KEYGUARD_SERVICE);
		return mKeyguardManager.inKeyguardRestrictedInputMode();

	}

	public static boolean isContainPinyinOrChar(String scr, String cc) {

		return false;
	}

	public static JSONObject createJSONObject(ArrayList<JSONObject> jList) {
		JSONObject json = new JSONObject();
		try {
			JSONArray jArray = new JSONArray();
			for (JSONObject jsonObject : jList) {
				jArray.put(jsonObject);
			}
			json.putOpt("results", jArray);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return json;
	}

	public static ArrayList<JSONObject> getJSONArrayList(JSONObject json) {
		ArrayList<JSONObject> arr = null;
		if (json != null) {
			JSONArray jArray = json.optJSONArray("results");
			if (jArray != null) {
				int count = jArray.length();
				arr = new ArrayList<JSONObject>();
				for (int i = 0; i < count; i++) {
					JSONObject j = jArray.optJSONObject(i);
					arr.add(j);
				}
			}
		}
		return arr;
	}

	static Toast toast = null;

	public static void showMsg(Activity activity, final String msg) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(SOApplication.getAppContext(), "",
							Toast.LENGTH_SHORT);
				}
				toast.setText(msg);
				toast.show();
			}
		});
	}

	// 给定日期返回星期
	public static String dayForWeek(String pTime) throws Exception {
		String week;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}

		switch (dayForWeek) {
		case 1:
			week = "星期一";
			break;
		case 2:
			week = "星期二";
			break;
		case 3:
			week = "星期三";
			break;
		case 4:
			week = "星期四";
			break;
		case 5:
			week = "星期五";
			break;
		case 6:
			week = "星期六";
			break;
		case 7:
			week = "星期日";
			break;
		default:
			week = "时间错误";
			break;
		}

		return week;
	}

	// 计算两个日期相距多少天
	public static int daysBetween(String date1, String date2)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (date1 == null || date1.trim().length() == 0 || date2 == null || date2.trim().length() == 0) {
			return 0;
		}
		Date smdate = sdf.parse(date1);
		Date bdate = sdf.parse(date2);
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	static String DivId;

	public final static String getDivId() {

		if (DivId == null) {
			Context c = SOApplication.getAppContext();
			final TelephonyManager tm = (TelephonyManager) c
					.getSystemService(Context.TELEPHONY_SERVICE);
			DivId = tm.getDeviceId()
					+ android.provider.Settings.Secure.getString(
							c.getContentResolver(),
							android.provider.Settings.Secure.ANDROID_ID);

		}
		return DivId;
	}

	public static void installApk(final Activity act, final String fileName) {
		act.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				File apkfile = new File(fileName);
				if (apkfile.exists()) {
					// 通过Intent安装APK文件
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
							"application/vnd.android.package-archive");
					act.startActivity(i);
				}
			}
		});
	}

	public static String byteToKMGB(float b) {
		String kmgb = "";
		DecimalFormat df = new DecimalFormat("###.00");

		if (b < 1024) {
			kmgb = b + "B";
		} else if (b < 1024 * 1024) {
			kmgb = df.format(b / 1024) + "KB";
		} else if (b < 1024 * 1024 * 1024) {
			kmgb = df.format(b / 1024 / 1024) + "MB";
		} else {
			kmgb = df.format(b / 1024 / 1024 / 1024) + "GB";
		}
		return kmgb;
	}

	public static String getMobilePhoneNumber(String phone) {

		if (phone == null || phone.length() < 11) {
			return null;
		}

		if (phone.startsWith("+86")) {
			phone = phone.substring(3);
		}

		if (phone.startsWith("0")) {
			phone = phone.substring(1);
		}

		phone = phone.replaceAll(" ", "").replaceAll("-", "");
		if (phone.length() < 11) {
			return null;
		}
		if (!phone.startsWith("1")) {
			return null;
		}
		return phone;
	}

	public static final boolean isChineseCharacter(String chineseStr) {
		char[] charArray = chineseStr.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			// 是否是Unicode编码,除了"�"这个字符.这个字符要另外处理
			if ((charArray[i] >= '\u0000' && charArray[i] < '\uFFFD')
					|| ((charArray[i] > '\uFFFD' && charArray[i] < '\uFFFF'))) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @author paulburke
	 */
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
									   String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}
}
