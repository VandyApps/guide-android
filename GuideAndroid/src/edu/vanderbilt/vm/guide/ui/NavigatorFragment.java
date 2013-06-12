package edu.vanderbilt.vm.guide.ui;

import com.actionbarsherlock.app.SherlockFragment;

public abstract class NavigatorFragment extends SherlockFragment {
    
    protected IGraphMapper mMapper;
    
    public void setGraphMapper(IGraphMapper mapper) {
        mMapper = mapper;
    }

}
