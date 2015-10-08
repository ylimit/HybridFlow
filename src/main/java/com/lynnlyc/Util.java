package com.lynnlyc;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import com.ibm.wala.cast.ipa.callgraph.CAstAnalysisScope;
import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.cast.ir.translator.TranslatorToCAst.Error;
import com.ibm.wala.cast.js.html.MappedSourceModule;
import com.ibm.wala.cast.js.html.WebPageLoaderFactory;
import com.ibm.wala.cast.js.html.WebUtil;
import com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil;
import com.ibm.wala.cast.js.loader.JavaScriptLoader;
import com.ibm.wala.cast.js.loader.JavaScriptLoaderFactory;
import com.ibm.wala.cast.js.translator.CAstRhinoTranslatorFactory;
import com.ibm.wala.cast.loader.AstMethod;
import com.ibm.wala.cast.types.AstMethodReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.SourceModule;
import com.ibm.wala.ipa.callgraph.impl.Everywhere;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.IRFactory;
import com.ibm.wala.ssa.SSAOptions;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.Predicate;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.Pair;
import com.lynnlyc.app.AppManager;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

public class Util {
    public static final Logger LOGGER = Logger.getLogger("Webview-flow");

	public static final String loadUrlSig =
			"<android.webkit.WebView: void loadUrl(java.lang.String)>";
	public static final String addJavascriptInterfaceSig =
			"<android.webkit.WebView: void addJavascriptInterface(java.lang.Object,java.lang.String)>";
	public static final String setWebViewClientSig =
			"<android.webkit.WebView: void setWebViewClient(android.webkit.WebViewClient)>";
    public static final String setWebChromeClientSig =
            "<android.webkit.WebView: void setWebChromeClient(android.webkit.WebChromeClient)>";

	public static List<SootMethod> findEntryPoints() {
		ArrayList<SootMethod> entries = new ArrayList<SootMethod>();
		for (SootClass cls : Scene.v().getApplicationClasses()) {
			if (cls.isAbstract()) continue;

			for (SootMethod m : cls.getMethods()) {
				entries.add(m);
			}
		}
//		System.out.println(entries.size());
//		System.out.println(entries);
		return entries;
	}

    public static String getTimeString() {
        long timeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-hhmmss");
        Date date = new Date(timeMillis);
        return sdf.format(date);
    }

	public static void logException(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		Util.LOGGER.warning(sw.toString());
	}

	public static boolean isSimilarClass(SootClass c1, SootClass c2) {
		if (c1 == null || c2 == null) {
			return false;
		}
		if (c1 == c2) {
			return true;
		}
		while (c1.hasSuperclass()) {
			c1 = c1.getSuperclass();
			if (c1 == c2) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSimilarMethod(SootMethod m1, SootMethod m2) {
		if (m1 == null || m2 == null) {
			return false;
		}
		if (m1 == m2) {
			return true;
		}
		if ((m1.getSubSignature().equals(m2.getSubSignature())) && Util.isSimilarClass(m1.getDeclaringClass(), m2.getDeclaringClass())) {
			return true;
		}
		return false;
	}

	public static String trimQuotation(String value) {
		int len = value.length();
		int st = 0;
		char[] val = value.toCharArray();    /* avoid getfield opcode */

		while ((st < len) && (val[st] <= ' ' || val[st] == '"')) {
			st++;
		}
		while ((st < len) && (val[len - 1] <= ' ' || val[len - 1] == '"')) {
			len--;
		}
		return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
	}
}