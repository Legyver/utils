package com.legyver.util.graphrunner.ctx.shared;

import com.legyver.core.exception.CoreException;
import com.legyver.util.graphrunner.GraphExecutedCommand;
import com.legyver.util.wrapadapt.TypedCommand;
import com.legyver.util.wrapadapt.WrapAdapter;

public class SharedContextCommand implements GraphExecutedCommand<SharedMapCtx>, TypedCommand<String> {
	@Override
	public void execute(String nodeName, SharedMapCtx sharedMapCtx) throws CoreException {
		if (sharedMapCtx != null) {
			Object value = sharedMapCtx.getValue();
			new WrapAdapter(nodeName, value).execute(this);
		}
	}

}
