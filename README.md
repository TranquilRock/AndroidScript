# AndroidScript

# RESTART VERSION









## DevLog
- Looking forward to replacing OpenCV with self defined matching functions, as OpenCV takes up way too many resoure to provide simply functionality.

## Updates

- 2022/08/13
    - FGO script is now out of maintenance, but shall still works.
    - Found memory leak in foreground service, update version will explicitly stopForeground to free
      them.

## Intro/簡介

- Slides: https://docs.google.com/presentation/d/1p2pku1bjBfWljaol40b3c5a5rb4wGgAlyKgmD7n5bRQ/edit?usp=sharing

- Demo video
    1. UI: https://www.youtube.com/watch?v=CwXp0ZrnUg0
    2. Game(FGO): https://www.youtube.com/watch?v=SLK26wOAvS8

- English
    - This is a simple kit to auto play FGO/ArKnights.
    - There is also easy interface to develop auto script.
    - The resources for FGO/ArKnights are currently for Traditional-Chinese server, one may need to
      replace the resource (Only those with Chinese character on) with same name to run on different
      server.
    - The resources will reside in Android/media/AndroidScript folder.
    - Made with friends for own use, no guarantee for maintenance.
    - If you think this kit did help, please leave a Star↗↗↗↗

- 中文
    - FGO/明日方舟的戰鬥腳本，讓雙手從周回中解放~~
    - 也支援用圖形介面編輯任意用途的腳本
    - FGO/明日方舟的相關檔案擷取自繁中服，如果要應用在不同伺服器需要更換有包含文字的資源圖檔
    - 檔案資源會建立在Android/media/AndroidScript
    - 出於興趣及自身需求製作，沒辦法保證更新、維護週期
    - 如果覺得有幫助請不吝給個Star↗↗↗↗

## Usage/使用說明

- English
    - FGO
    - ArKnights
        - Only support PRTS-replay, check before script start.
        - Set repetition and whether restore sanity before start.
    - Basic
        - Define your own script (Need to select screen orientation in Menu)
        - Load own resources into "Android/media/AndroidScript/Basic/"
        - Support the following command(For details please check out code in util/interpreter.java)
            - Exit
                - End the whole script
            - Log
                - Show log on the floating widget
            - JumpTo
                - Jump to the specified Line/Tag
            - Wait
                - Wait a specific ms
            - Call
                - Call another script
            - Tag
                - Specify a place to be jumped
            - Return
                - End current script with return value
            - ClickPic
                - Click the bottom-right corner of the provided picture on screen(The resolution of
                  provide picture need to be the same as you screen)
            - Click
                - Click the provided (x,y)
            - CallArg
                - Call another script with argument(s)
            - IfGreater
                - Compare two value, if the former larger than the latter, $R = 0, otherwise $R = 1
            - IfSmaller
                - Compare two value, if the former smaller than the latter, $R = 0, otherwise $R = 1
            - Add
                - Add the variable
            - Subtract
                - Subtract the variable
            - Var
                - Assign a value to the variable
            - Check
                - Check color of the specific location
            - Swipe
                - Swipe between two provided location
            - Compare
                - Compare region with provided picture(Resolution can be different)

- 中文
    - FGO
        - 關卡前置
        - 御主技能
    - ArKnights
        - 只適用代理作戰，啟動腳本前先勾選代理。
        - 設定重複次數、選擇是否自動恢復理智(體力)。
    - Basic
        - 自行編排指令，可以把資源檔案放到"Android/media/AndroidScript/Basic/"當中讓腳本使用
        - 支援以下指令(詳細格式參考util/interpreter.java)
            - Exit
                - 結束整個腳本
            - Log
                - 顯示訊息在浮動工具
            - JumpTo
                - 跳至指定行數、Tag
            - Wait
                - 等待時間
            - Call
                - 呼叫另一個腳本
            - Tag
                - 標註位置讓JumpTo跳躍
            - Return
                - 結束當前腳本並回傳數值
            - ClickPic
                - 點擊提供圖片的右下角(解析度需與畫面相同)
            - Click
                - 點擊指定座標
            - CallArg
                - 呼叫另一個腳本並附帶參數
            - IfGreater
                - 比較兩個數值，若前者大於後者，設定$R為0，否則$R為1
            - IfSmaller
                - 比較兩個數值，若前者小於後者，設定$R為0，否則$R為1
            - Add
                - 將指定變數加上一個數值
            - Subtract
                - 將指定變數減去一個數值
            - Var
                - 給予變數一個數值
            - Check
                - 檢查某個座標的顏色
            - Swipe
                - 滑過兩個指定座標之間
            - Compare
                - 比較指定區域是否符合提供圖片(解析度不須相同)

## Resource/資源

- English
    - FGO/
    - ArKnights/
        - AutoFight.txt
            - Script txt, need not to modify.
        - AutoFightEat.txt
            - Script txt, need not to modify.
        - LevelUp.png
            - Check level-up page.
            - Chinese-character on it, replace with same resolution.
        - PressEnterOperation.png
            - Check before operation page.
            - Chinese-character on it, replace with same resolution.
        - PressOperationEnd.png
            - Check operation end page.
            - Chinese-character on it, replace with same resolution.
        - PressRestore.png
            - Check Originium restore page.
            - Need not to modify.
        - PressRestoreMedicine.png
            - Check Sanity restore page.
            - Chinese-character on it, replace with same resolution.
        - PressStartOperation.png
            - Check Mission Start button.
            - Chinese-character on it, replace with same resolution.

- 中文
    - FGO/
    - ArKnights/
        - AutoFight.txt
            - 戰鬥腳本，不需要更改
        - AutoFightEat.txt
            - 戰鬥腳本，不需要更改
        - LevelUp.png
            - 判斷開始關卡的圖片
            - 帶有文字，更換時注意要維持相同解析度
        - PressEnterOperation.png
            - 判斷關卡結束的圖片
            - 帶有文字，更換時注意要維持相同解析度
        - PressOperationEnd.png
            - 判斷進入關卡的圖片
            - 帶有文字，更換時注意要維持相同解析度
        - PressRestore.png
            - 判斷吃源石的圖片
            - 在不同伺服器不需更換
        - PressRestoreMedicine.png
            - 判斷吃理智劑的圖片
            - 帶有文字，更換時注意要維持相同解析度
        - PressStartOperation.png
            - 判斷開始關卡的圖片
            - 帶有文字，更換時注意要維持相同解析度

## Class Graph (V1)

- (Click pic for higher resolution)
- ![](https://i.imgur.com/SauAxNZ.png)
