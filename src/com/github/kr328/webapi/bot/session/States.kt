package com.github.kr328.webapi.bot.session

interface State

class PreclashSendFileState(val deleteMessage: Long) : State
class PreclashDownloadState : State