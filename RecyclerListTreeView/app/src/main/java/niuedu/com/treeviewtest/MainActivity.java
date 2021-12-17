package niuedu.com.treeviewtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.niuedu.ListTree;

public class MainActivity extends AppCompatActivity {

    //保存数据的集合
    private ListTree tree = new ListTree();
    //从ListTreeAdapter派生的Adapter
    ExampleListTreeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber()+"]");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //使用Android原生的RecyclerView即可
        RecyclerView listView = findViewById(R.id.listview);

        //创建后台数据：一棵树
        //创建组们，是root node，所有parent为null
        //ListTree.TreeNode groupNode1 = tree.addNode(null, "特别关心", R.layout.contacts_group_item);
        //ListTree.TreeNode groupNode2 = tree.addNode(null, "我的好友", R.layout.contacts_group_item);
        //ListTree.TreeNode groupNode3 = tree.addNode(null, "朋友", R.layout.contacts_group_item);
        //ListTree.TreeNode groupNode4 = tree.addNode(null, "家人", R.layout.contacts_group_item);
        //ListTree.TreeNode groupNode5 = tree.addNode(null, "同学", R.layout.contacts_group_item);
        ListTree.TreeNode groupNode6 = tree.addNode(null, "example", R.layout.contacts_group_item);
        ListTree.TreeNode groupNode7 = tree.addNode(null, "telephony", R.layout.contacts_group_item);

        //第二层
        ExampleListTreeAdapter.ContactInfo contact;
        // contacts_normal 人像小圖
        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.contacts_normal);

        //contact = new ExampleListTreeAdapter.ContactInfo(bitmap, "王二", "[在线]我是王二");
        //ListTree.TreeNode contactNode1 = tree.addNode(groupNode1, contact, R.layout.contacts_contact_item);
        //tree.addNode(groupNode1, contact, R.layout.contacts_contact_item);
        ////再添加一个
        //contact = new ExampleListTreeAdapter.ContactInfo(bitmap, "王二", "[在线]我是王二");
        //ListTree.TreeNode contactNode2 = tree.addNode(groupNode2, contact, R.layout.contacts_contact_item);
        //ListTree.TreeNode contactNode3 = tree.addNode(groupNode5, contact, R.layout.contacts_contact_item);
        ////再添加一个
        //// bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.contacts_normal);
        //contact = new ExampleListTreeAdapter.ContactInfo(bitmap, "王三", "[离线]我没有状态");
        //tree.addNode(groupNode2, contact, R.layout.contacts_contact_item);
        //tree.addNode(groupNode5, contact, R.layout.contacts_contact_item);

        contact = new ExampleListTreeAdapter.ContactInfo("task.py");
        tree.addNode(groupNode6, contact, R.layout.contacts_contact_item);

        contact = new ExampleListTreeAdapter.ContactInfo("task_message.py");
        tree.addNode(groupNode7, contact, R.layout.contacts_contact_item);
        contact = new ExampleListTreeAdapter.ContactInfo("task_rsst3_app.py");
        tree.addNode(groupNode7, contact, R.layout.contacts_contact_item);


        //第三层
        // bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.contacts_normal);
        //contact = new ExampleListTreeAdapter.ContactInfo(bitmap, "东邪", "[离线]出来还价");
        //ListTree.TreeNode n = tree.addNode(contactNode1, contact, R.layout.contacts_contact_item);
        //n.setShowExpandIcon(false);
        ////再添加一个
        ////bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.contacts_normal);
        //contact = new ExampleListTreeAdapter.ContactInfo(bitmap, "东邪", "[离线]出来还价");
        //n = tree.addNode(contactNode2, contact, R.layout.contacts_contact_item);
        //n.setShowExpandIcon(false);
        ////再添加一个
        ////bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.contacts_normal);
        //contact = new ExampleListTreeAdapter.ContactInfo(bitmap, "李圆圆", "[离线]昨天出门没出去");
        //n = tree.addNode(contactNode2, contact, R.layout.contacts_contact_item);
        //n.setShowExpandIcon(false);

        adapter = new ExampleListTreeAdapter(tree);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber()+"]");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("YAO 1111 [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber()+"]");

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_del_selected) {
            //删除选中的Nodes，删一个Node时会将其子孙一起删掉
            tree.removeCheckedNodes();
            adapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.action_expand_all) {
            //展开所有的node
            tree.expandAllNodes();
            adapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.action_collapse_all) {
            //收起所有的node
            tree.collapseAllNodes();
            adapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.action_iterate_all_checked) {
            tree.enumCheckedNodes(node -> {
                if (node.getData() instanceof ExampleListTreeAdapter.ContactInfo) {
                    ExampleListTreeAdapter.ContactInfo info = (ExampleListTreeAdapter.ContactInfo) node.getData();
                    info.setTitle("enum "+info.getTitle());
                }
            });
            if(adapter !=null) {
                adapter.notifyDataSetChanged();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //响应某个Node上的快捷菜单的选择事件
    //@Override
    //public boolean onMenuItemClick(MenuItem item) {
    //    System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber()+"]");

    //    switch (item.getItemId()) {
    //    case R.id.action_add_item:
    //        //向当前行增加一个儿子
    //        ListTree.TreeNode node = adapter.getCurrentNode();
    //        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.contacts_normal);
    //        ExampleListTreeAdapter.ContactInfo contact = new ExampleListTreeAdapter.ContactInfo(
    //            bitmap, "New contact", "[离线]我没有状态");
    //        ListTree.TreeNode childNode = tree.addNode(node, contact, R.layout.contacts_contact_item);
    //        adapter.notifyTreeItemInserted(node, childNode);
    //        return true;
    //    case R.id.action_clear_children:
    //        //清空所有的儿子们
    //        node = adapter.getCurrentNode();
    //        Pair<Integer, Integer> range = tree.clearDescendant(node);
    //        adapter.notifyItemRangeRemoved(range.first, range.second);
    //        return true;
    //    default:
    //        return false;
    //    }
    //}
}

