package com.legyver.utils.graphrunner.ctx.shared;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.graphrunner.GraphExecutedCommand;
import com.legyver.utils.wrapadapt.TypedCommand;
import com.legyver.utils.wrapadapt.WrapAdapter;

/**
 * Utility GraphExecutedCommand that operates on a shared context of type SharedMapCtx
 */
public class SharedContextCommand implements GraphExecutedCommand<SharedMapCtx>, TypedCommand<String> {
	@Override
	public void execute(String nodeName, SharedMapCtx sharedMapCtx) throws CoreException {
		if (sharedMapCtx != null) {
			Object value = sharedMapCtx.getValue();
			new WrapAdapter(nodeName, value).execute(this);
		}
	}

}
