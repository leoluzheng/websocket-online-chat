package com.wdg.chatonlinewebsocket.service.impl;

import com.wdg.chatonlinewebsocket.bean.DialogueDetail;
import com.wdg.chatonlinewebsocket.dao.DialogueDetailMapper;
import com.wdg.chatonlinewebsocket.service.DialogueDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dialogueDetailService")
@SuppressWarnings("all")
public class DialogueDetailServiceImp implements DialogueDetailService {

	@Autowired
	private DialogueDetailMapper dialogueDetailMapper;
	
	@Override
	public int add(DialogueDetail dialogueDetail) {
		return this.dialogueDetailMapper.add(dialogueDetail);
	}

	@Override
	public int update(DialogueDetail dialogueDetail) {
		return this.dialogueDetailMapper.update(dialogueDetail);
	}

	@Override
	public int deleteById(Integer id) {
		return this.dialogueDetailMapper.deleteById(id);
	}

	@Override
	public DialogueDetail queryDialogueDetailById(Integer id) {
		return this.dialogueDetailMapper.queryDialogueDetailById(id);
	}
}
