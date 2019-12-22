package com.wdg.chatonlinewebsocket.service;


import com.wdg.chatonlinewebsocket.bean.Dialogue;

import java.util.List;

/**
 * Description:
 * @author: WuDG
 *
 */
@SuppressWarnings("all")
public interface DialogueService {
	int add(Dialogue dialogue);
    int update(Dialogue dialogue);
    int deleteById(Integer id);
    Dialogue queryDialogueById(Integer id);
    List<Dialogue> selectByDialogue(Dialogue record);
}
