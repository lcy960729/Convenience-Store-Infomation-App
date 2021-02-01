import cx_Oracle

class Info:
    def __init__(self,store,event,name,price,img):
        self.store=store
        self.event=event
        self.name=name
        self.price=price
        self.img=img
        pass  
    def __del__(self):
        pass
    def toString(self):
        return "[store = {}] [event = {}] [name = {}] [price = {}] [img = {}]".format(self.store, self.event, self.name, self.price, self.img)
        pass  
    
def openOracleSQL():
    global db
    global cursor
    try:
        db = cx_Oracle.connect("c##SE/1234@localhost:1521/orcl")
        cursor = db.cursor()
        print("OK")
    except Exception as ex: # 에러 종류
        print('에러가 발생 했습니다', ex) 
    pass

def excuteSQL(SQL):
    pass

def commit():
    global db
    db.commit()
    pass

def insertOracleSQL(productnum, category, store, event, name, price, src):
    global cursor
    try:
        sql = "INSERT INTO GOODS values(:sproductnum,:sname,:sprice,:sstore,:scategory,:ssrc,:sevent,0,0,SYSDATE)"
        cursor.execute(sql,sproductnum = productnum, sname = name, sprice = price, sstore = store, scategory = category, ssrc = src, sevent = event)
        print("OK")
    except Exception as ex: # 에러 종류
        print('에러가 발생 했습니다', ex) 
    pass


def closeOracleSQL():
    global db
    db.close()
    pass