package com.github.blockand;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.blockand.dao.BlockListDao;
import com.github.blockand.event.UpdateDBEvent;
import com.github.blockand.ext.CursorRecyclerViewAdapter;
import com.github.blockand.ext.ItemTouchHelperViewHolder;
import com.github.blockand.ext.MyListCursorAdapter;
import com.github.blockand.ext.SimpleDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    private BlockListDao blockListDao;
    Cursor cursor;
    private MyListCursorAdapter mAdapter;
    public static final float ALPHA_FULL = 1.0f;

    @Subscribe
    public void onEvent(UpdateDBEvent event) {
        if (mAdapter != null)
            mAdapter.changeCursor(cursor = blockListDao.loadAll(-1, -1), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final ImageView space = (ImageView) findViewById(R.id.space);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        blockListDao = new BlockListDao(this);
        cursor = blockListDao.loadAll(-1, -1);
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this, true));

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyListCursorAdapter(this, cursor);
        mAdapter.setCountChangeListener(new CursorRecyclerViewAdapter.CountChangeListener() {
            @Override
            public void onChange(int i) {
                if (i == 0) {
                    space.setVisibility(View.VISIBLE);
                } else {
                    space.setVisibility(View.GONE);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startService(new Intent(getBaseContext(), RingtoneService.class));
                View view1 = View.inflate(view.getContext(), R.layout.dialog_add_block, null);
                final EditText number = (EditText) view1.findViewById(R.id.number);
                final AlertDialog mDialog = new AlertDialog.Builder(MainActivity.this).setView(view1)
                        .setTitle(R.string.add_block_number)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                blockListDao.addNumber(null, number.getText().toString().replaceAll("\\s", "").trim());
                                mAdapter.changeCursor(cursor = blockListDao.loadAll(-1, -1), 0);
                                mRecyclerView.scrollToPosition(cursor.getCount() - 1);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                MyListCursorAdapter.ViewHolder viewHolder1 = (MyListCursorAdapter.ViewHolder) viewHolder;
                blockListDao.flow();
                blockListDao.excludeId = viewHolder1._id;
                Snackbar.make(((MyListCursorAdapter.ViewHolder) viewHolder).mTextView1, R.string.deleted_contact, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                blockListDao.excludeId = -1;
                                mAdapter.changeCursor(cursor = blockListDao.loadAll(-1, -1), 0);
                            }
                        }).show();
                mAdapter.changeCursor(cursor = blockListDao.loadAll(-1, -1), 0);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                // We only want the active item to change
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    if (viewHolder instanceof ItemTouchHelperViewHolder) {
                        // Let the view holder know that this item is being moved or dragged
                        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                        itemViewHolder.onItemSelected();
                    }
                }

                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                viewHolder.itemView.setAlpha(ALPHA_FULL);

                if (viewHolder instanceof ItemTouchHelperViewHolder) {
                    // Tell the view holder it's time to restore the idle state
                    ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                    itemViewHolder.onItemClear();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            new AlertDialog.Builder(this).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    blockListDao.deleteAll();
                    mAdapter.changeCursor(cursor = blockListDao.loadAll(-1, -1), 0);
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).setMessage("Sure?").show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        blockListDao.flow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        blockListDao.writableDatabase.close();
        EventBus.getDefault().unregister(this);
    }
}
