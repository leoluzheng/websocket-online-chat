package com.wdg.chatonlinewebsocket.controller;

import com.wdg.chatonlinewebsocket.bean.DialogueDetail;
import com.wdg.chatonlinewebsocket.service.DialogueDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dialogueDetail")
@SuppressWarnings("all")
public class DialogueDetailController {

	@Autowired
	private DialogueDetailService dialogueDetailService;
	
	@GetMapping("/selectOne")
	public DialogueDetail selectOne(Integer id) {
		return this.dialogueDetailService.queryDialogueDetailById(id);
	}
}
