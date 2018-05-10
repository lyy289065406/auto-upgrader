package exp.au.convert;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.au.Config;
import exp.au.bean.ldm.PatchInfo;
import exp.libs.envm.Regex;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.tpl.Template;

/**
 * <PRE>
 * 页面/应用信息转换器
 * </PRE>
 * <B>PROJECT：</B> exp-certificate
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Convertor {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(Convertor.class);
	
	public static void main(String[] args) {
		System.out.println(Convertor.toPage(Config.PATCH_DIR));
	}
	
	/** 私有化构造函数 */
	protected Convertor() {}
	
	/**
	 * 根据升级补丁目录生成升级导航页面
	 * @param patchDirPath 升级补丁目录路径
	 * @return 升级导航页面
	 */
	public static boolean toPage(String patchDirPath) {
		File patchDir = new File(patchDirPath);
		List<String> tables = toTables(patchDir);
		
		Template tpl = new Template(Config.PAGE_TPL, Config.DEFAULT_CHARSET);
		tpl.set("tables", StrUtils.concat(tables, ""));
		tpl.set("time", TimeUtils.getSysDate());
		return FileUtils.write(Config.PAGE_PATH, 
				tpl.getContent(), Config.DEFAULT_CHARSET, false);
	}
	
	/**
	 * 根据升级补丁目录生成每个应用的升级补丁导航表单
	 * @param patchDir 升级补丁目录
	 * @return 每个应用的升级补丁导航表单
	 */
	private static List<String> toTables(File patchDir) {
		List<String> tables = new LinkedList<String>();
		Template tpl = new Template(Config.TABLE_TPL, Config.DEFAULT_CHARSET);
		
		File[] appDirs = patchDir.listFiles();
		for(File appDir : appDirs) {
			if(appDir.isFile()) {
				continue;
			}
			
			tpl.set("name", appDir.getName());
			tpl.set("rows", StrUtils.concat(toRows(appDir), ""));
			tables.add(tpl.getContent());
		}
		return tables;
	}
	
	/**
	 * 根据某个应用的升级补丁目录生成其每个升级补丁的导航栏
	 * @param appDir 某个应用的升级补丁
	 * @return 每个升级补丁的导航栏
	 */
	private static List<String> toRows(File appDir) {
		List<String> rows = new LinkedList<String>();
		Template tpl = new Template(Config.ROW_TPL, Config.DEFAULT_CHARSET);
		
		File[] verDirs = appDir.listFiles();
		for(File verDir : verDirs) {
			if(verDir.isFile()) {
				continue;
			}
			
			tpl.set("app_name", appDir.getName());
			tpl.set("version", verDir.getName());
			tpl.set("time", TimeUtils.toStr(verDir.lastModified()));
			rows.add(tpl.getContent());
		}
		return ListUtils.reverse(rows);	// 版本倒序
	}
	
	/**
	 * 从页面提取应用补丁列表信息
	 * @param pageSource 页面源码
	 * @param appName 应用名称
	 * @return 补丁列表信息
	 */
	@SuppressWarnings("unchecked")
	public static List<PatchInfo> getPatchInfos(String pageSource, String appName) {
		List<PatchInfo> patchInfos = new LinkedList<PatchInfo>();
		try {
			Document doc = DocumentHelper.parseText(pageSource);
			Element html = doc.getRootElement();
			Element body = html.element("body");
			Element div = body.element("div");
			Iterator<Element> divs = div.elementIterator("div");
			while(divs.hasNext()) {
				Element table = divs.next().element("table");
				String name = table.attributeValue("id");
				if(appName.equals(name)) {
					patchInfos = toPatchInfos(table);
					break;
				}
			}
		} catch (Exception e) {
			log.error("从页面提取应用 [{}] 的补丁列表信息失败:\r\n{}", appName, pageSource, e);
		}
		return patchInfos;
	}
	
	@SuppressWarnings("unchecked")
	private static List<PatchInfo> toPatchInfos(Element table) {
		List<PatchInfo> patchInfos = new LinkedList<PatchInfo>();
		final String REGEX = "\\[([^\\]]+)\\] VERSIONS: (.*)";
		String appName = "";
		
		Element tbody = table.element("tbody");
		Iterator<Element> trs = tbody.elementIterator();
		while(trs.hasNext()) {
			Element tr = trs.next();
			List<Element> ths = tr.elements();
			String key = ths.get(0).getTextTrim();
			String val = StrUtils.trimAll(ths.get(1).getText());
			
			if("SOFTWARE-NAME".equals(key)) {
				appName = val;
				
			} else {
				List<String> groups = RegexUtils.findGroups(key, REGEX);
				String time = groups.get(1);
				String version = groups.get(2);
				
				List<String> brackets = RegexUtils.findBrackets(val, "href=\"([^\"]+)\"");
				String zipURL = brackets.get(0);
				String txtURL = brackets.get(1);
				String md5URL = brackets.get(2);
				
				// TODO
				/*
		click to download (
           <a href="./patches/bilibili-plugin/4.1/bilibili-plugin-patch-4.1.zip">zip</a>, 
           <a href="./patches/bilibili-plugin/4.1/bilibili-plugin-patch-4.1.zip.txt">txt</a>, 
           <a href="./patches/bilibili-plugin/4.1/MD5.html">MD5</a> 
         ) <a href="./patches/bilibili-plugin/4.1/update.xml" hidden="hidden" />
				 */
			}
		}
		return patchInfos;
	}
	
	
}
