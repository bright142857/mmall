<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
 <title>Ajax异步上传图片</title>
 <-- 引入jQuery异步上传js文件 -->
 <script type="text/javascript" src="http://libs.baidu.com/jquery/1.11.1/jquery.min.js"></script>

 <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.form/4.2.2/jquery.form.min.js" integrity="sha384-FzT3vTVGXqf7wRfy8k4BiyzvbNfeYjK+frTVqZeNDFl8woCbF0CYG6g2fMEFFo/i" crossorigin="anonymous"></script>

 <!-- Ajax异步上传图片 -->
 <script type="text/javascript">
     function uploadPic() {
         // 上传设置
         var options = {
             // 规定把请求发送到那个URL
             url: "/manage/product/upload.do",
             // 请求方式
             type: "post",
             // 服务器响应的数据类型
             dataType: "json",
             // 请求成功时执行的回调函数
             success: function(data, status, xhr) {
                 // 图片显示地址
                 $("#allUrl").attr("src", data.path);
             }
         };

         $("#jvForm").ajaxSubmit(options);
     }
 </script>
</head>
<body>
<form id="jvForm" action="o_save.shtml" method="post" enctype="multipart/form-data">
 <table>
  <tbody>
  <tr>
   <td width="20%">
    <span>*</span>
    上传图片(90x150尺寸):</td>
   <td width="80%">
    注:该尺寸图片必须为90x150。
   </td>
  </tr>
  <tr>
   <td width="20%"></td>
   <td width="80%">
    <img width="100" height="100" id="allUrl"/>
    <!-- 在选择图片的时候添加事件，触发Ajax异步上传 -->
    <input name="upload_file" type="file" onchange="uploadPic()"/>
   </td>
  </tr>
  </tbody>
 </table>
</form>
</body>
</html>
    