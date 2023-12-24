package com.github.vultr.core.windows;

import com.github.vultr.core.type.TableColumn;
import com.github.vultr.core.service.VultrService;
import com.github.vultr.core.util.DateUtil;
import com.github.vultr.core.util.GroovyUtil;
import com.github.vultr.core.util.JsonUtil;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MainFrame extends JFrame {


    @Autowired
    private VultrService vultrService;

    @Getter
    private JTable table;

    // 创建默认表格模型并设置列名
    final DefaultTableModel tableModel = new DefaultTableModel() {{
        setColumnIdentifiers(Arrays.stream(TableColumn.values()).map(it -> it.name()).toArray());
    }};


    public MainFrame() {
        initView();
    }


    /**
     * 刷新列表
     */
    public void refreshList() {
        var ret = vultrService.instances();
        //清空
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.removeRow(0);
        }
        //添加
        int size = (int) GroovyUtil.runScript(ret, "instances.size()");
        for (int i = 0; i < size; i++) {
            Map<String, Object> item = (Map<String, Object>) GroovyUtil.runScript(ret, "instances[" + i + "]");

            Optional.ofNullable(item.get("date_created")).ifPresent((it) -> {
                long time = (System.currentTimeMillis() - DateUtil.toTime((String) item.get("date_created"))) / 1000;
                item.put("date_created", DateUtil.format(time));
            });

            //计划转换为每小时
            Optional.ofNullable(item.get("plan")).ifPresent((it) -> {
                item.put("plan", vultrService.getHoursCost(String.valueOf(it)) + " / 小时");
            });
            tableModel.addRow(toTableRow(item));
        }
    }

    private JButton removeBtn() {
        JButton button = new JButton("删除实例");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                Integer row = table.getSelectedRow();
                if (row == null || row < 0) {
                    JOptionPane.showMessageDialog(MainFrame.this, "请先选中要删除的项");
                    return;
                }
                Map<TableColumn, Object> rowData = tableRowToData(row);
                int option = JOptionPane.showConfirmDialog(MainFrame.this, "是否删除实例 : \r\n %s".formatted(JsonUtil.toJson(rowData, true)), "提示", JOptionPane.YES_NO_OPTION);
                if (option == 0) {
                    var ret = vultrService.deleteInstances(String.valueOf(rowData.get(TableColumn.id)));
                    System.out.println(ret);
                    MainFrame.this.refreshList();
                }


            }
        });
        return button;
    }

    private JButton refreshBtn() {
        // 创建按钮组1
        JButton refreshBtn = new JButton("刷新实例");
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainFrame.this.refreshList();
            }
        });

        return refreshBtn;
    }

    private JButton createBtn() {
        // 创建按钮组1
        JButton refreshBtn = new JButton("创建实例");
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println("创建实例...");
            }
        });

        return refreshBtn;
    }

    private Object[] toTableRow(Map<String, Object> item) {
        return Arrays.stream(TableColumn.values()).map(it -> {
            return item.get(it.getColumnName());
        }).map(it -> {
            return it != null ? it : "";
        }).toArray();
    }

    private Map<TableColumn, Object> tableRowToData(int row) {
        var ret = new HashMap<TableColumn, Object>();
        for (int i = 0; i < TableColumn.values().length; i++) {
            TableColumn value = TableColumn.values()[i];
            ret.put(value, tableModel.getValueAt(row, i));
        }
        return ret;
    }


    private void initView() {

        setTitle("Vultr - Console");

        // 设置主框架分辨率为 640x480
        setSize(800, 600);


        // 创建JTable并使用模型
        table = new JTable(tableModel);


        // 创建按钮组2
        JButton button4 = new JButton("Button 4");
        JButton button5 = new JButton("Button 5");

        // 创建选项框1，并设置流式布局和对齐方式
        JPanel buttonPanel1 = new JPanel();
        buttonPanel1.setLayout(new BoxLayout(buttonPanel1, BoxLayout.X_AXIS));
        buttonPanel1.setAlignmentX(Component.LEFT_ALIGNMENT);

        buttonPanel1.add(refreshBtn());
        buttonPanel1.add(Box.createRigidArea(new Dimension(10, 0))); // 添加垂直间隔
        buttonPanel1.add(createBtn());
        buttonPanel1.add(Box.createRigidArea(new Dimension(10, 0))); // 添加垂直间隔
        buttonPanel1.add(removeBtn());


        // 创建选项框2，并设置流式布局和对齐方式
        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.setLayout(new BoxLayout(buttonPanel2, BoxLayout.X_AXIS));
        buttonPanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel2.add(button4);
        buttonPanel2.add(button5);

        // 创建顶部面板，并设置垂直盒式布局
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // 添加选项框1和选项框2到顶部面板
        topPanel.add(buttonPanel1);
        topPanel.add(buttonPanel2);

        // 创建主面板，并设置边界布局
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // 将主面板添加到主框架
        add(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // 窗口启动居中
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int windowWidth = getWidth();
        int windowHeight = getHeight();
        int x = (screenWidth - windowWidth) / 2;
        int y = (screenHeight - windowHeight) / 2;
        setLocation(x, y);
    }


}
