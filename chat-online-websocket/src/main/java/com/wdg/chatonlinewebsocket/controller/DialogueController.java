package com.wdg.chatonlinewebsocket.controller;

import com.wdg.chatonlinewebsocket.bean.Dialogue;
import com.wdg.chatonlinewebsocket.service.DialogueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dialogue")
@SuppressWarnings("all")
public class DialogueController {

	@Autowired
	private DialogueService dialogueService;
	
	@GetMapping("/selectOne")
	public Dialogue selectOne(Integer id) {
		return this.dialogueService.queryDialogueById(id);
	}
}
