export function createRequestDto(data, type) {
    const baseFields = {
        id: data.id,
        type: type,
        status: data.status || data.requestStatus,
        date: data.date || data.requestDate,
        mentor:  data.mentor,
    };

    if(type === 'SESSION') {
        return {
            ...baseFields,
            startTime: data.startTime,
            endTime: data.endTime,
            mentees: data.mentees,
            title: data.title,
            location: data.location,
            description: data.description
        };
    } else if (type === 'RELATIONSHIP') {
        return {
            ...baseFields,
            message: data.message,
            mentee: data.mentee,
            senderId: data.senderId,

        };
    }
}

function canAccept(request, userId) {
    if (request.type === 'SESSION') {
        return request.status === 'PENDING' && request.mentees.includes(userId);
    } else if (request.type === 'RELATIONSHIP') {
        return request.status === 'PENDING' && request.mentee.id === userId;
    }
    return false;
}
