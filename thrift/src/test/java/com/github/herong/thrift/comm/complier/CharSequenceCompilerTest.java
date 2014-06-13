package com.github.herong.thrift.comm.complier;

import java.util.Arrays;
import java.util.Random;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

public class CharSequenceCompilerTest {
	private final CharSequenceCompiler<IRemoteService> compiler = new CharSequenceCompiler<IRemoteService>(
			getClass().getClassLoader(), Arrays.asList(new String[] {
					"-target", "1.5" }));
	private static final Random random = new Random();
	private static String PACKAGE_NAME = "com.github.herong.thrift.comm.complier";
	private static String classNameSuffix = "RemoteService";

	/**
	 * @return random hex digits with a '_' prefix
	 */
	private String digits() {
		return '_' + Long.toHexString(random.nextLong());
	}

	public IRemoteService ins() {
		try {
			// generate semi-secure unique package and class names
			final String packageName = PACKAGE_NAME;
			final String className = "GenRemoteService";
			final String qName = packageName + '.' + className;
			// generate the source class as String
			final String source = "package com.github.herong.thrift.comm.complier;                 \n"
					+ "                                                                \n"
					+ "public class GenRemoteService implements IRemoteService {       \n"
					+ "                                                                \n"
					+ "	@Override                                                     \n"
					+ "	public String execute(String msg) {                           \n"
					+ "		System.out.println(\"生成的类名:\" + getClass().getName()); \n"
					+ "		System.out.println(\"调用方法:execute:\" + msg);            \n"
					+ "		return \"resp\" + msg;                                    \n"
					+ "	}                                                             \n"
					+ "                                                                \n"
					+ "}";

			// compile the generated Java source
			final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<JavaFileObject>();
			Class<IRemoteService> compiledFunction = compiler.compile(qName,
					source, errs, new Class<?>[] { IRemoteService.class });
			return compiledFunction.newInstance();
		} catch (CharSequenceCompilerException e) {
			log(e.getDiagnostics());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void log(final DiagnosticCollector<JavaFileObject> diagnostics) {
		final StringBuilder msgs = new StringBuilder();
		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics
				.getDiagnostics()) {
			msgs.append(diagnostic.getMessage(null)).append("\n");
		}
		System.out.println(msgs.toString());
	}

	public static void main(String[] args) throws ClassCastException,
			CharSequenceCompilerException, InstantiationException,
			IllegalAccessException {
		IRemoteService rs = new CharSequenceCompilerTest().ins();
		rs.execute("测试");
		Class<? extends IRemoteService> clazz = rs.getClass();
		System.out.println("Our class: " + clazz.getName());
		System.out.println("Classloader: " + clazz.getClassLoader());

	}
}
