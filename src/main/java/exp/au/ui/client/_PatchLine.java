package exp.au.ui.client;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import exp.au.utils.UIUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.SwingUtils;

class _PatchLine extends JPanel {

	private static final long serialVersionUID = 3115712607344507109L;

	private final static String UNDOWN = "  未下载  ";
	
	private final static String DOWN = "  已下载  ";
	
	private final static String UNISTALL = "  未安装  ";
	
	private final static String INSTALL = "  已安装  ";
	
	private String patchName;
	
	private JLabel patchLabel;
	
	private JRadioButton downBtn;
	
	private JRadioButton installBtn;
	
	protected _PatchLine(String patchName, String releaseTime) {
		super(new BorderLayout());
		
		this.patchName = patchName;
		String tagName = StrUtils.concat("[", releaseTime, "]  ", patchName);
		this.patchLabel = new JLabel(tagName);
		
		this.downBtn = new JRadioButton(UNDOWN);
		downBtn.setEnabled(false);
		
		this.installBtn = new JRadioButton(UNISTALL);
		installBtn.setEnabled(false);
		
		initLayout();
	}

	/**
	 * 初始化布局
	 */
	private void initLayout() {
		add(patchLabel, BorderLayout.CENTER);
		add(SwingUtils.getHGridPanel(downBtn, installBtn), BorderLayout.EAST);
		SwingUtils.addBorder(this);
	}

	/**
	 * 标记为已下载
	 * @param toLog 是否打印日志
	 */
	protected void markDown(boolean toLog) {
		downBtn.setText(DOWN);
		downBtn.setSelected(true);
		downBtn.setForeground(Color.BLUE);
		
		if(toLog == true) {
			UIUtils.toConsole("下载补丁 [", patchName, "] 成功");
		}
	}
	
	/**
	 * 标记为已安装
	 * @param toLog 是否打印日志
	 */
	protected void markInstall(boolean toLog) {
		installBtn.setText(INSTALL);
		installBtn.setSelected(true);
		installBtn.setForeground(Color.BLUE);
		
		if(toLog == true) {
			UIUtils.toConsole("安装补丁 [", patchName, "] 成功");
		}
	}
	
	
}
