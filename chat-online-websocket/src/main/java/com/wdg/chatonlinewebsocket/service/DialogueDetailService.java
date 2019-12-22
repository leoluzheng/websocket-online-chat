package com.wdg.chatonlinewebsocket.service;


import com.wdg.chatonlinewebsocket.bean.DialogueDetail;

/**
 * Description:
 * @author: WuDG
 *
 */
@SuppressWarnings("all")
public interface DialogueDetailService {
	int add(DialogueDetail dialogueDetail);
    int update(DialogueDetail dialogueDetail);
    int deleteById(Integer id);
    DialogueDetail queryDialogueDetailById(Integer id);
}
