import requests
import json
url = 'https://securegw-stage.paytm.in/merchant-status/getTxnStatus'
payload = {
    "MID": "www.mywbsite.fr",
    "ORDERID": "playzi02115984898118",
    "CHECKSUMHASH": "7WrjSXFnIKedQOxQEDETJ1BdrXW6bQAXZ34MwqHT4b8rrbsQC5aCJQzyYEq/I20ogcPthPiIaLVMWROm+qvmwtOywf4BZTPw5OmWxxTCKpg=",
}
data = "JsonData="+json.dumps(payload)
print(data)
headers = {"contentType":"application/json"}
r = requests.post(url, data=data, headers=headers)
print(r.content)
