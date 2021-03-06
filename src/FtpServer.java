import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class FtpServer extends Thread {
	private Socket socketClient;
	private int counter;
	public static String initDir; // 保存服务器线程运行时所在的工作目录
	public static ArrayList users = new ArrayList();
	public static ArrayList usersInfo = new ArrayList();

	public FtpServer() {
		FtpConsole fc = new FtpConsole();
		fc.start();/*
					 * Java 虚拟机调用线程 run 方法,结果是两个线程同时运行: 当前线程 (方法 start 返回的线程)
					 * 和另一个线程 (执行方法 run 的线程)
					 */
		loadUsersInfo(); // 加载

		counter = 1;
		int i = 0;
		try {

			// 监听21号端口,21口用于控制,20口用于传数据
			ServerSocket s = new ServerSocket(21);
			for (;;) {
				// 接受客户端请求
				Socket incoming = s.accept();
//				BufferedReader in = new BufferedReader(new InputStreamReader(
//						incoming.getInputStream()));
//				BufferedWriter out = new BufferedWriter
//						(new OutputStreamWriter(incoming.getOutputStream(),"GBK"));
//				out.write("220 准备为您服务" + ",你是当前第  " + counter + " 个登陆者!");// 命令正确的提示
//				out.flush();
				// 创建服务线程
				FtpHandler h = new FtpHandler(incoming,i+1);
				h.start();
				users.add(h); // 将此用户线程加入到这个 ArrayList 中
				counter++;
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// loadUsersInfo方法
	// 把文件中的用户信息(用户名,密码,路径)加载到UserInfo 这个 ArrayList 中
	public void loadUsersInfo() {
		String s = getClass().getResource("user.cfg").toString();
		System.out.println(s);
		/*
																 * getResource()按指
																 * 定名查找资源,返回一资源的
																 * URL
																 * 值;getClass
																 * ()返回一个对象的运
																 * 行时间类;
																 */
		s = s.substring(5, s.length());// 子串开始6，扩展到 s.length()的位置。 ///???
		int p1 = 0; // 放 | 的索引
		int p2 = 0; // 放 | 后一位的索引
		System.out.println(s);
		if (new File(s).exists())// 测试当前 File 是否存在
		{
			System.out.println("文件存在");
			try {
				BufferedReader fin = new BufferedReader(new InputStreamReader(
						new FileInputStream(s)));
				String line; // 从文件中取一行存于此
				String field; // 放 | 前 line 的子串
				int i = 0;
				// 第一个while 作用为读所有行
				while ((line = fin.readLine()) != null)// 达流尾则为 null
				{
					UserInfo tempUserInfo = new UserInfo();
					p1 = 0;
					p2 = 0;
					i = 0;
					if (line.startsWith("#"))// 如以#开始,返回ture
						continue;
					// 第二个while 作用为load 文件中一行的信息
					while ((p2 = line.indexOf("|", p1)) != -1)// 从p1开始查,返回 |
																// 第一次出现的索引,没有返回-1
					{
						field = line.substring(p1, p2);// 从p1 ~ p2-1
						p2 = p2 + 1;
						p1 = p2; // 新p2
						switch (i) {
						case 0:
							tempUserInfo.user = field;
							break;
						case 1:
							tempUserInfo.password = field;
							break;
						case 2:
							tempUserInfo.workDir = field;
							break;
						}
						i++;
					}
					usersInfo.add(tempUserInfo);
					System.out.println(tempUserInfo.user);
					System.out.println(tempUserInfo.password);
					System.out.println(tempUserInfo.workDir);
				}
				fin.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// main方法
	/*
	 * 主函数完成服务器端口的侦听和服务线程的创建,服务器的初始工作目录是由程序运行时用户输入的，缺省为C盘的根目录
	 */
	public static void main(String[] args) {
		if (args.length != 0) {
			initDir = args[0];
		} else {
			initDir = "C:\\Users\\gedehua666\\Desktop\\ssh\\myftp\\src";
		}
		FtpServer ftpServer = new FtpServer();

	}
}
