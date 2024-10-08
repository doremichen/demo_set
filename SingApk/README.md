# Singed App with platform key
下载 keytool-importkeypair 工具（https://gitcode.net/mirrors/getfatday/keytool-importkeypair?utm_source=csdn_github_accelerator），

 使用sdk的security文件生成对应平台的key：

./keytool-importkeypair -k [jks文件名] -p [jks的密码] -pk8 platform.pk8 -cert platform.x509.pem -alias [jks的别名]

如：
./keytool-importkeypair -k ./SignDemo.jks -p 123456 -pk8 platform.pk8 -cert platform.x509.pem -alias SignDemo

 注：在下载的keytool-importkeypair目录的空白处右键，选择“git bash here”（windows安装git），执行以上命令。
***
Reference: https://www.cnblogs.com/blogs-of-lxl/p/9233285.html
