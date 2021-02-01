#!/usr/bin/env python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.support import expected_conditions as EC
import time
import urllib.request
from selenium.webdriver.support.ui import WebDriverWait
import oracle
driver = webdriver.Chrome('chromedriver.exe')

def openChrome():
    driver.maximize_window()
    driver.get("https://m.search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=%ED%8E%B8%EC%9D%98%EC%A0%90+%ED%96%89%EC%82%AC")
    pass

def crawl():
    oracle.openOracleSQL()
    cnt=0
    k=1
    time.sleep(5)
    for count in range(2,7):
        driver.find_element_by_xpath("//*[@id='ct']/section[1]/div/div[3]/div/div/ul/li[{}]/a".format(count)).click() 
        category= str(driver.find_element_by_xpath('//*[@id="ct"]/section[1]/div/div[3]/div/div/ul/li[{}]/a/span'.format(count)).text)
        num= driver.find_element_by_xpath('//*[@id="ct"]/section[1]/div/div[6]/div/span/span[3]').text
        time.sleep(3)
        for j in range(1,11):
            cnt=cnt+1
            sel=cnt%3
            if sel==0: sel=3
            print(sel)
            time.sleep(1)
            for i in range(1,5):
                store = str(driver.find_element_by_xpath('//*[@id="ct"]/section[1]/div/div[5]/div[1]/div/div[{}]/div/ul/li[{}]/div/a[2]/div/div/span[1]'.format(str(sel),str(i))).text) 
                event = str(driver.find_element_by_xpath('//*[@id="ct"]/section[1]/div/div[5]/div[1]/div/div[{}]/div/ul/li[{}]/div/a[2]/div/strong/span[2]'.format(str(sel),str(i))).text) + " EVENT"
                name =   str(driver.find_element_by_xpath('//*[@id="ct"]/section[1]/div/div[5]/div[1]/div/div[{}]/div/ul/li[{}]/div/a[2]/div/strong/span[1]'.format(str(sel),str(i))).text)
                price =   str(driver.find_element_by_xpath('//*[@id="ct"]/section[1]/div/div[5]/div[1]/div/div[{}]/div/ul/li[{}]/div/a[2]/div/p/em'.format(str(sel),str(i))).text)
                price = price.replace(",","")
                img_src = driver.find_element_by_xpath('//*[@id="ct"]/section[1]/div/div[5]/div[1]/div/div[{}]/div/ul/li[{}]/div/a[1]/img'.format(str(sel),str(i)))
                img = str(img_src.get_attribute('src')) 
            
                print('{0},{1},{2},{3},{4},{5}'.format(category,store,event,name,price,img)) 
                oracle.insertOracleSQL(k, category, store, event, name, price, img)
                k+=1
                pass  
            driver.find_element_by_xpath("//*[@id='ct']/section[1]/div/div[6]/div/a[2]").click() 
            oracle.commit()
            pass
        pass
    pass
    driver.close()


if __name__ == '__main__':
    openChrome()
    while True:
        crawl()
    pass