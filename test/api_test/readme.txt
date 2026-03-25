api_auto_test/                  # 项目根目录
├── api/                        # 接口层：封装所有接口请求
│   ├── __init__.py
│   ├── user_api.py             # 按业务模块拆分（如用户模块）
│   └── order_api.py            # 订单模块
├── config/                     # 配置层：存储全局配置
│   ├── __init__.py
│   ├── config.yaml             # 配置文件（环境、域名、账号等）
│   └── config.py               # 读取配置的工具类
├── data/                       # 数据层：测试数据/用例数据
│   ├── __init__.py
│   ├── test_data.yaml          # 测试用例数据
│   └── mock_data.py            # 模拟数据（可选）
├── logs/                       # 日志层：存储运行日志
│   └── test_run.log
├── report/                     # 报告层：测试报告输出
│   └── html/                   # HTML格式报告（如Allure/Pytest-HTML）
├── testcases/                  # 用例层：编写测试用例
│   ├── __init__.py
│   ├── conftest.py             # Pytest夹具（前置/后置操作）
│   ├── test_user.py            # 用户模块用例
│   └── test_order.py           # 订单模块用例
├── utils/                      # 工具层：通用工具函数
│   ├── __init__.py
│   ├── request_util.py         # 请求封装（如统一处理headers、超时）
│   ├── log_util.py             # 日志配置
│   └── assert_util.py          # 断言封装
├── .gitignore                  # Git忽略文件
├── pytest.ini                  # Pytest配置文件
├── requirements.txt            # 依赖包清单
└── run.py                      # 用例运行入口
根据该目录，给我写出在vscode执行创建目录的脚本