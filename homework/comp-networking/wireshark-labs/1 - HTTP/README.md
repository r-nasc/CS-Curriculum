# What to hand in

Having gotten our feet wet with the Wireshark packet sniffer in the introductory lab, we’re now ready to use Wireshark to investigate protocols in operation. In this lab, we’ll explore several aspects of the HTTP protocol: the basic GET/response interaction, HTTP message formats, retrieving large HTML files, retrieving HTML files with embedded objects, and HTTP authentication and security. [(Source)](http://www-net.cs.umass.edu/wireshark-labs/Wireshark_HTTP_v8.0.pdf)

### Answer the following questions, based on your Wireshark experimentation:  
1. Is your browser running HTTP version 1.0 or 1.1?  What version of HTTP is the server running? **HTTP/1.1 and HTTP/1.1**
2. What languages (if any) does your browser indicate that it can accept to the server? **en-US**
3. What is the IP address of your computer?  Of the gaia.cs.umass.edu server? **[Skipped] and 128.119.245.12**
4. What is the status code returned from the server to your browser? **200**
5. When was the HTML file that you are retrieving last modified at the server? **Last-Modified: Mon, 31 Jul 2023 05:59:01 GMT**
6. How many bytes of content are being returned to your browser? **81**
7. By inspecting the raw data in the packet content window, do you see any headers within the data that are not displayed in the packet-listing window?  If so, name one. **None**
8. Inspect the contents of the first HTTP GET request from your browser to the server.  Do you see an “IF-MODIFIED-SINCE” line in the HTTP GET? **No**
9. Inspect the contents of the server response. Did the server explicitly return the contents of the file?   How can you tell? **Yes. By the response body.**
10. Now inspect the contents of the second HTTP GET request from your browser to the server.  Do you see an “IF-MODIFIED-SINCE:” line in the HTTP GET? If so, what information follows the “IF-MODIFIED-SINCE:” header? **The date when it was last accessed (If-Modified-Since: Mon, 31 Jul 2023 05:59:01 GMT)**
11. What is the HTTP status code and phrase returned from the server in response to this second HTTP GET?  Did the server explicitly return the contents of the file?   Explain. **304 NOT MODIFIED. It didn't return the contents. The contents weren't changed after our cached version, so we don't need to retrieve it again.**