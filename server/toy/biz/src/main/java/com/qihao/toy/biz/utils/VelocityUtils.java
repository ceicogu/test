package com.qihao.toy.biz.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;

import java.io.StringWriter;
import java.util.Map;
import java.util.UUID;

//import java.util.UUID;

public class VelocityUtils {
	private static final Log log = LogFactory.getLog(VelocityUtils.class);

	private static VelocityEngine ve = new VelocityEngine();

	static {
		ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new NullLogChute());
		ve.setProperty(RuntimeConstants.VM_PERM_INLINE_LOCAL, true);
		ve.setProperty(RuntimeConstants.SET_NULL_ALLOWED, true);
		ve.setProperty(RuntimeConstants.VM_MAX_DEPTH, -1);
		ve.setProperty(RuntimeConstants.PARSE_DIRECTIVE_MAXDEPTH, -1);
		ve.setProperty(RuntimeConstants.DEFINE_DIRECTIVE_MAXDEPTH, -1);
		ve.init();
	}
	public static String evaluate(String vm) {
		return evaluate(vm, new VelocityContext());
	}

	public static String evaluate(String vm, Map<String, Object> parameter) {
		VelocityContext vc = new VelocityContext();
		for (Object o : parameter.keySet()) {
			vc.put(o.toString(), parameter.get(o));
		}
		return evaluate(vm, vc);
	}

	public static String evaluate(String vm, VelocityContext vc) {
		StringWriter sw = new StringWriter();
		try {
			Velocity.evaluate(vc, sw, "velocityUtils.evaluate." + UUID.randomUUID(), vm);
			return sw.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				sw.close();
			} catch (Exception e) {
				log.warn(e);
			}
		}
	}
	public static String evaluate(String vm, String templateName) {
		return evaluate(vm, new VelocityContext(), templateName);
	}

	public static String evaluate(String vm, Map<String, Object> parameter, String templateName) {
		VelocityContext vc = new VelocityContext();
		for (Object o : parameter.keySet()) {
			vc.put(o.toString(), parameter.get(o));
		}
		return evaluate(vm, vc, templateName);
	}

	public static String evaluate(String vm, VelocityContext vc, String templateName) {
		StringWriter sw = new StringWriter();
		try {
			// ve.evaluate(vc, sw, "velocityUtils.evaluate." +
			// UUID.randomUUID(), vm);
			ve.evaluate(vc, sw, templateName, vm);
			return sw.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				sw.close();
			} catch (Exception e) {
				log.warn(e);
			}
		}
	}

	public static String mergeTemplate(String templateName, String encoding, Map<String, Object> parameter) {
		VelocityContext context = new VelocityContext();
		for (Object o : parameter.keySet()) {
			context.put(o.toString(), parameter.get(o));
		}
		return mergeTemplate(templateName, encoding, context);
	}

	public static String mergeTemplate(String templateName, String encoding, Context context) {
		StringWriter sw = new StringWriter();
		try {
			ve.mergeTemplate(templateName, encoding, context, sw);
			return sw.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				sw.close();
			} catch (Exception e) {
				log.warn(e);
			}
		}
	}

}
