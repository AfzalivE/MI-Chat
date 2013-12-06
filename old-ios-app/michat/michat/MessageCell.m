//
//  MessageCell.m
//  michat
//
//  Created by Afzal Najam on 2013-09-24.
//  Copyright (c) 2013 Afzal Najam. All rights reserved.
//

#import "MessageCell.h"

@implementation MessageCell

@synthesize userNameLabel;
@synthesize messageLabel;
@synthesize timeLabel;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
