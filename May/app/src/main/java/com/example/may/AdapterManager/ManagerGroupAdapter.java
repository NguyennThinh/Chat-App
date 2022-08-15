package com.example.may.AdapterManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Interface.IGroupListener;
import com.example.may.Interface.IUserCListener;
import com.example.may.Model.Department;
import com.example.may.Model.Group;
import com.example.may.Model.User;
import com.example.may.R;
import com.example.may.View.EmployeeDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManagerGroupAdapter extends RecyclerView.Adapter<ManagerGroupAdapter.employeeViewHoler> {
    List<Group> arrGroup;
    private IGroupListener iGroupListener;

    public ManagerGroupAdapter(List<Group> arrGroup, IGroupListener iGroupListener) {
        this.arrGroup = arrGroup;
        this.iGroupListener = iGroupListener;
    }

    @NonNull
    @Override
    public employeeViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menager_group, parent, false);

        return new employeeViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull employeeViewHoler holder, int position) {
        Group group = arrGroup.get(position);

            Picasso.get().load(group.getGroupImage()).into(holder.imgGroup);

        holder.groupName.setText(group.getGroupName());


            holder.slParticipant.setText(group.getSlParticipant()+" thành viên");

        holder.layoutUser.setOnClickListener(v -> {
            showPopup(v, group);
        });
    }




    @Override
    public int getItemCount() {
        return arrGroup.size();
    }

    static class employeeViewHoler extends RecyclerView.ViewHolder {
        private CircleImageView imgGroup;
        private TextView groupName, slParticipant;
        private LinearLayout layoutUser;
        public employeeViewHoler(@NonNull View itemView) {
            super(itemView);
            imgGroup = itemView.findViewById(R.id.img_group);
            groupName = itemView.findViewById(R.id.group_name);
            slParticipant = itemView.findViewById(R.id.sl_participant);
            layoutUser = itemView.findViewById(R.id.layoutUser);
        }
    }


    public void showPopup(View v, Group group) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.information:
                    iGroupListener.InfoGroup(group);

                    break;
                case R.id.delete_group:
                    iGroupListener.deleteGroup(group.getGroupID());
                    break;
            }
            return false;
        });
        popupMenu.inflate(R.menu.group_manager);

        Object menuHelper;
        Class[] argTypes;
        try {
            @SuppressLint("DiscouragedPrivateApi") Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            assert menuHelper != null;
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception ignored) {

        }
        popupMenu.show();
    }

}
