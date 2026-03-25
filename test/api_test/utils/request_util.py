import requests
from config.config import get_base_url
from utils.log_util import logger
from utils.file_util import file_util

class RequestUtil:
    """请求工具类（封装requests）"""
    def __init__(self):
        self.base_url = get_base_url()
        self.session = requests.Session()  # 会话保持

    def request(self, method, url, headers=None, params=None, json=None, data=None ,files=None, timeout=10):
        full_url = f"{self.base_url}{url}"
        try:
            response = self.session.request(
                method=method,
                url=full_url,
                headers=headers,
                params=params,
                json=json,
                data=data,
                files=files,
                timeout=timeout
            )
            logger.info(f"请求成功 | {method.upper()} {full_url} | 状态码: {response.status_code}")
            return response
        except Exception as e:
            logger.error(f"请求失败 | {method.upper()} {full_url} | 异常: {str(e)}")
            raise e
        finally:
            # 无论请求成功/失败，都关闭文件对象
            if files:
                file_util.close_file_params(files)

    def post(self, url, headers=None, params=None, json=None,data=None, files=None):
        """POST请求封装"""
        return self.request("post", url, headers, params, json, data,files)

# 全局请求对象
request_util = RequestUtil()