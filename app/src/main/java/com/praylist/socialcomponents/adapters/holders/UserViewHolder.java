/*
 *  Copyright 2017 Rozdoum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.praylist.socialcomponents.adapters.holders;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.praylist.socialcomponents.R;
import com.praylist.socialcomponents.enums.FollowState;
import com.praylist.socialcomponents.managers.FollowManager;
import com.praylist.socialcomponents.managers.ProfileManager;
import com.praylist.socialcomponents.managers.listeners.OnObjectChangedListener;
import com.praylist.socialcomponents.managers.listeners.OnObjectChangedListenerSimple;
import com.praylist.socialcomponents.model.Profile;
import com.praylist.socialcomponents.utils.GlideApp;
import com.praylist.socialcomponents.utils.ImageUtil;
import com.praylist.socialcomponents.views.FollowButton;

/**
 * Created by Alexey on 03.05.18.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = UserViewHolder.class.getSimpleName();

    private Context context;
    private ImageView photoImageView;
    private TextView nameTextView;
    private FollowButton followButton;

    private ProfileManager profileManager;

    private Activity activity;

    public UserViewHolder(View view, final Callback callback, Activity activity) {
        super(view);
        this.context = view.getContext();
        this.activity = activity;
        profileManager = ProfileManager.getInstance(context);

        nameTextView = view.findViewById(R.id.nameTextView);
        photoImageView = view.findViewById(R.id.photoImageView);
        followButton = view.findViewById(R.id.followButton);

        view.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (callback != null && position != RecyclerView.NO_POSITION) {
                callback.onItemClick(getAdapterPosition(), v);
            }
        });

        followButton.setOnClickListener(v -> {
            if (callback != null) {
                callback.onFollowButtonClick(getAdapterPosition(), followButton);
            }
        });
    }

    public void bindData(String profileId) {
        profileManager.getProfileSingleValue(profileId, createProfileChangeListener());
    }

    public void bindData(Profile profile) {
        fillInProfileFields(profile);
    }

    private OnObjectChangedListener<Profile> createProfileChangeListener() {
        return new OnObjectChangedListenerSimple<Profile>() {
            @Override
            public void onObjectChanged(Profile obj) {
                fillInProfileFields(obj);
            }
        };
    }

    protected void fillInProfileFields(Profile profile) {
        nameTextView.setText(profile.getUsername());

        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId != null) {
            if (!currentUserId.equals(profile.getId())) {
                FollowManager.getInstance(context).checkFollowState(currentUserId, profile.getId(), followState -> {
                    followButton.setVisibility(View.VISIBLE);
                    followButton.setState(followState);
                });
            } else {
                followButton.setState(FollowState.MY_PROFILE);
            }
        } else {
            followButton.setState(FollowState.NO_ONE_FOLLOW);
        }

        if (profile.getPhotoUrl() != null) {
            ImageUtil.loadImage(GlideApp.with(activity), profile.getPhotoUrl(), photoImageView);
        }
    }

    public interface Callback {
        void onItemClick(int position, View view);

        void onFollowButtonClick(int position, FollowButton followButton);
    }

}