package com.tcpip147.lsql.eclipse.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

public class KeyStrokeAccelators {

	public static List<Integer> get(String commandId) {
		IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getAdapter(IBindingService.class);
		Binding[] bindings = bindingService.getBindings();
		List<Integer> keyStrokes = new ArrayList<>();
		for (Binding binding : bindings) {
			if (binding.getParameterizedCommand() != null) {
				if (commandId.equals(binding.getParameterizedCommand().getId())) {
					if ("org.eclipse.ui.defaultAcceleratorConfiguration".equals(binding.getSchemeId())) {
						if (binding.getTriggerSequence() instanceof KeySequence keySequence) {
							for (KeyStroke stroke : keySequence.getKeyStrokes()) {
								keyStrokes.add(SWTKeySupport.convertKeyStrokeToAccelerator(stroke));
							}
						}
					}
				}
			}
		}
		return keyStrokes;
	}

	public static boolean match(KeyEvent e, List<Integer> strokes) {
		int event = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
		for (int stroke : strokes) {
			if (event == stroke) {
				return true;
			}
		}
		return false;
	}
}
