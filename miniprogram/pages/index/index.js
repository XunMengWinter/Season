//index.js
const app = getApp()

Page({
  data: {
    cards:[]
  },

  onLoad: function () {
    if (!wx.cloud) {
      wx.redirectTo({
        url: '../chooseLib/chooseLib',
      })
      return
    }

    this.getCards()
  },

  onGetOpenid: function () {
    // 调用云函数
    wx.cloud.callFunction({
      name: 'login',
      data: {},
      success: res => {
        console.log('[云函数] [login] user openid: ', res.result.openid)
        app.globalData.openid = res.result.openid
        wx.navigateTo({
          url: '../userConsole/userConsole',
        })
      },
      fail: err => {
        console.error('[云函数] [login] 调用失败', err)
        wx.navigateTo({
          url: '../deployFunctions/deployFunctions',
        })
      }
    })
  },

  onPullDownRefresh(e){
    this.getCards()
  },

  getCards(e) {
    const that = this
    wx.request({
      url: 'https://icemono.oss-cn-hangzhou.aliyuncs.com/calendar/all.json',
      data: {},
      success: res => {
        that.setData({
          cards: res.data.data
        })
      },
      fail: res => {

      }
    })
  }


})