Imports System.Collections.Generic
Imports System.Data
Imports System.Data.SqlClient
Imports System.IO
Imports System.Web.Script.Serialization
Imports System.Web.Configuration

Partial Class B4AToSQL
    Inherits System.Web.UI.Page
    Dim conn As System.Data.SqlClient.SqlConnection =  New System.Data.SQLClient.SQLConnection ("Data Source=localhost;Initial Catalog=B4ADB;Persist Security Info=True;User ID=sa;Password=****")

    Protected Sub Page_Load(sender As Object, e As EventArgs) Handles Me.Load
        Dim C As String
        Dim sr As StreamReader = New StreamReader(Request.InputStream, Encoding.UTF8)
        C = Request.QueryString("query")
        If c Is Nothing Then
            c = sr.ReadToEnd()
        End If
        Try
            Dim cmd As New SqlCommand(C, conn)
            conn.Open()
            Dim rdr As SqlDataReader = cmd.ExecuteReader(CommandBehavior.CloseConnection)
            Dim list As New List(Of Dictionary(Of String, Object))()
            While rdr.Read()
                Dim d As New Dictionary(Of String, Object)(rdr.FieldCount)
                For i As Integer = 0 To rdr.FieldCount - 1
                    d(rdr.GetName(i)) = rdr.GetValue(i)
                Next
                list.Add(d)
            End While
            Dim j As New JavaScriptSerializer()
            Response.Write(j.Serialize(list.ToArray()))
        Catch ex As Exception
            Response.TrySkipIisCustomErrors = True
            Response.StatusCode = 500
            '預防直接訪問網頁
            Dim y As String = If(Request.QueryString("query"), "")
            If y = "" Then
                Response.Write("Error錯誤")
            Else
                Response.Write("Error錯誤")
                'Response.Write("Error錯誤Query=" & C & vbLf)
            End If
        End Try
        Response.[End]()
    End Sub
End Class
