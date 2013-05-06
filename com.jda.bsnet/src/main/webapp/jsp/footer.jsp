<div class="footertext">Copyright © 1995-2013 Argo Inc. All Rights Reserved. Designated trademarks and brands are the property of their respective owners.
<%
String userName  = (String)session.getAttribute("userName");
if(userName != null && userName != "") {
%>
<a href="bsnet/login/logout"  id="logoutbutton" class="btn btn-success" >Logout</a>
<%}%>
</div>



