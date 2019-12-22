package com.wdg.chatonlinewebsocket.service.impl;

import com.wdg.chatonlinewebsocket.bean.Dialogue;
import com.wdg.chatonlinewebsocket.dao.DialogueMapper;
import com.wdg.chatonlinewebsocket.service.DialogueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dialogueService")
@SuppressWarnings("all")
public class DialogueServiceImp implements DialogueService {

	@Autowired
	private DialogueMapper dialogueMapper;
	
	@Override
	public int add(Dialogue dialogue) {
		return this.dialogueMapper.add(dialogue);
	}

	@Override
	public int update(Dialogue dialogue) {
		return this.dialogueMapper.update(dialogue);
	}

	@Override
	public int deleteById(Integer id) {
		return this.dialogueMapper.deleteById(id);
	}

	@Override
	public List<Dialogue> selectByDialogue(Dialogue record) {
		return dialogueMapper.selectByDialogue(record);
	}

	@Override
	public Dialogue queryDialogueById(Integer id) {
		return this.dialogueMapper.queryDialogueById(id);
	}
}
